package com.github.kotlintelegrambot.dispatcher

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.dispatcher.handlers.ErrorHandler
import com.github.kotlintelegrambot.dispatcher.handlers.Handler
import com.github.kotlintelegrambot.dispatcher.new.buildRedirectedUpdate
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.errors.TelegramError
import com.github.kotlintelegrambot.extensions.filters.Filter
import com.github.kotlintelegrambot.logging.LogLevel
import com.github.kotlintelegrambot.types.DispatchableObject
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

open class Dispatcher<Ctx> internal constructor(
    private val updatesChannel: Channel<DispatchableObject>,
    private val logLevel: LogLevel,
    coroutineDispatcher: CoroutineDispatcher,
    val chatContextProvider: ChatContextProvider,
) : HandleCollector<Ctx> {

    internal lateinit var bot: Bot

    private val commandHandlers = linkedSetOf<Handler>()
    private val errorHandlers = arrayListOf<ErrorHandler>()

    private val scope: CoroutineScope = CoroutineScope(coroutineDispatcher)
    @Volatile
    private var job: Job? = null

    internal fun startCheckingUpdates() {
        job?.cancel()
        job = scope.launch { checkQueueUpdates() }
    }

    private suspend fun checkQueueUpdates() {
        while (true) {
            when (val item = updatesChannel.receive()) {
                is Update -> {
                    val chatStateProvider = wrapAsChatContextSource(item, chatContextProvider)
                    item.withChatContextSource(chatStateProvider)
                    try {
                        handleUpdate(item)
                        if (!item.consumed) {
                            println("HANDLER NOT FOUND FOR UPDATE: $item")
                        }
                    } catch (e: Exception) {
                        println("EXCEPTION IN HANDLER: $e")
                        e.printStackTrace()
                    }
                }

                is TelegramError -> handleError(item)
                else -> Unit
            }
            yield()
        }
    }

    fun on(filter: Filter, func: HandleCollector<Ctx>.() -> Unit) {
        val filterHandler = object : HandleCollector<Ctx> {
            override fun addHandler(handler: Handler) {
                commandHandlers.add(
                    object : Handler {
                        override fun checkUpdate(update: Update): Boolean {
                            return filter.checkFor(update) && handler.checkUpdate(update)
                        }

                        override suspend fun handleUpdate(bot: Bot, update: Update) {
                            handler.handleUpdate(bot, update)
                        }
                    }
                )
            }

            override fun addErrorHandler(errorHandler: ErrorHandler) {
                throw UnsupportedOperationException("Error handlers are not supported for filters")
            }
        }
        func(filterHandler)
    }

    override fun addHandler(handler: Handler) {
        commandHandlers.add(handler)
    }

    fun removeHandler(handler: Handler) {
        commandHandlers.remove(handler)
    }

    override fun addErrorHandler(errorHandler: ErrorHandler) {
        errorHandlers.add(errorHandler)
    }

    fun removeErrorHandler(errorHandler: ErrorHandler) {
        errorHandlers.remove(errorHandler)
    }

    private suspend fun handleUpdate(update: Update) {


        commandHandlers
            .asSequence()
            .filter { !update.consumed }
            .filter {
                it.checkUpdate(update)
            }
            .forEach {
                try {
                    it.handleUpdate(bot, update)
                    update.chatContextSource().flush()

                    if (update.hasRedirection()) {
                        handleUpdate(buildRedirectedUpdate(update))
                        return
                    }
                } catch (throwable: Throwable) {
                    if (logLevel.shouldLogErrors()) {
                        throwable.printStackTrace()
                    }
                }
            }
    }

    private fun handleError(error: TelegramError) {
        errorHandlers.forEach { handleError ->
            try {
                handleError(bot, error)
            } catch (throwable: Throwable) {
                if (logLevel.shouldLogErrors()) {
                    throwable.printStackTrace()
                }
            }
        }
    }

    internal fun stopCheckingUpdates() {
        job?.cancel()
    }
}
