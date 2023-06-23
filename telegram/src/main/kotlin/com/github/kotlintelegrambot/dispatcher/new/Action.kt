@file:Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")

package com.github.kotlintelegrambot.dispatcher.new

import org.omg.CORBA.Object
import java.lang.Long
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentSkipListSet
import kotlin.system.exitProcess

class Action<T>(val actionCode: String, val clazz: Class<T>) {

    init {
        if (codes.put(actionCode, actionCode) != null) {
            throw Error("Duplicate action code: $actionCode")
        }
    }

    companion object {
        private val codes = ConcurrentHashMap<String, String>()
    }


    fun toString(data: T): String {
        val dataParts = clazz.declaredFields
            .map { field ->
                field.isAccessible = true
                val value = field.get(data) ?: return@map ""

                when (value) {
                    is UUID -> value.toString()
                    is LocalDate -> value.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                    is BigDecimal -> value.setScale(2)
                    is Int -> value.toString()
                    else -> value
                }
            }

        return "$actionCode:${dataParts.joinToString(":")}"
    }

    fun parseData(data: String): T {
        val dataParts = data.split(":").drop(1)

        val instance = clazz.constructors.first { it.parameterCount == dataParts.size }
            .newInstance(
                *clazz.declaredFields.mapIndexed { idx, field ->
                    field.isAccessible = true

                    if (dataParts[idx] == "") return@mapIndexed null

                    when (field.type) {
                        UUID::class.java -> UUID.fromString(dataParts[idx])
                        LocalDate::class.java -> LocalDate.parse(
                            dataParts[idx],
                            DateTimeFormatter.ofPattern("yyyyMMdd")
                        )

                        BigDecimal::class.java -> BigDecimal(dataParts[idx])
                        Int::class.java -> dataParts[idx].toInt()
                        Integer::class.java -> dataParts[idx].toInt()
                        Long::class.java -> dataParts[idx].toLong()
                        else -> dataParts[idx]
                    }
                }.toTypedArray()
            )

        @Suppress("UNCHECKED_CAST")
        return (instance as T)
    }

    public fun Action<T>.data(buttonData: T) = toString(buttonData)
}
