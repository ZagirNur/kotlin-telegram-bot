package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.dispatcher.ChatContext
import com.github.kotlintelegrambot.entities.Update

internal class CommonHandler<T : ChatContext>(
    private val filter: Update.() -> Boolean,
    private val handleMessage: suspend UpdateContext<T>.() -> Unit
) : Handler {

    override fun checkUpdate(update: Update) = filter(update)

    override suspend fun handleUpdate(bot: Bot, update: Update) {
        val messageHandlerEnv = UpdateContext<T>(bot, update)
        handleMessage(messageHandlerEnv)
    }
}

data class UpdateContext<T : ChatContext>(
    val bot: Bot,
    val update: Update,
) {
    val ctx: T
        get() = update.chatContextSource().getChatContext()

    fun redirectTo(command: String? = null, buttonData: String? = null) {
        update.redirectTo(command, buttonData)
    }
}
