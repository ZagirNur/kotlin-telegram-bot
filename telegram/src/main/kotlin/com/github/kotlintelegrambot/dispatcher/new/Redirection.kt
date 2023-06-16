package com.github.kotlintelegrambot.dispatcher.new

import com.github.kotlintelegrambot.entities.CallbackQuery
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.entities.from

data class Redirection(
    val command: String? = null,
    val buttonData: String? = null,
)

fun buildRedirectedUpdate(update: Update): Update {

    var newUpdate: Update? = null

    val redirection = update.redirectAndClear()
    if (redirection.command != null) {

        newUpdate = update.copy(
            message = update.message?.copy(
                text = "/${redirection.command}",
                from = update.from
            ) ?: Message(
                text = "/${redirection.command}",
                from = update.from,
                chat = update.callbackQuery?.message?.chat!!,
                date = update.callbackQuery.message.date,
                messageId = update.callbackQuery.message.messageId
            ),
            callbackQuery = null
        )
    }
    if (redirection.buttonData != null) {
        newUpdate = update.copy(
            message = null,
            callbackQuery = update.callbackQuery?.copy(
                data = redirection.buttonData,
                from = update.from
            ) ?: CallbackQuery(
                id = "",
                data = redirection.buttonData,
                from = update.from,
                chatInstance = update.callbackQuery?.chatInstance ?: "",
            )
        )
    }
    if (redirection == Redirection()) {
        return update
    }
    if (newUpdate == null) {
        throw IllegalStateException("Invalid redirection: $redirection")
    }
    newUpdate.redirectAndClear()
    return newUpdate
}

