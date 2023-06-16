package com.github.kotlintelegrambot.dispatcher

import com.github.kotlintelegrambot.dispatcher.handlers.ErrorHandler
import com.github.kotlintelegrambot.dispatcher.handlers.HandleError
import com.github.kotlintelegrambot.dispatcher.handlers.Handler

interface HandleCollector<T> {

    fun addHandler(handler: Handler)
    fun addErrorHandler(errorHandler: ErrorHandler)
}
