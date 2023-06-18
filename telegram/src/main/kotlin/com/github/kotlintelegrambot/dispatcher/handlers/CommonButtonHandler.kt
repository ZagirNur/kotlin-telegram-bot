package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.dispatcher.ChatContext
import com.github.kotlintelegrambot.dispatcher.new.Action
import com.github.kotlintelegrambot.entities.Update

internal class CommonButtonHandler<T : ChatContext, A>(
    private val action: Action<A>,
    private val filter: Update.() -> Boolean,
    private val handleMessage: suspend UpdateButtonContext<T, A>.() -> Unit
) : Handler {

    override fun checkUpdate(update: Update) = filter(update)

    override suspend fun handleUpdate(bot: Bot, update: Update) {

        checkNotNull(update.callbackQuery)
        val callbackQueryHandlerEnv = UpdateButtonContext<T, A>(
            bot = bot,
            update = update,
            btnData = action.parseData(update.callbackQuery.data)
        )

        handleMessage(callbackQueryHandlerEnv)

        val callbackQueryId = update.callbackQuery.id
        bot.answerCallbackQuery(
            callbackQueryId = callbackQueryId,
            text = "Привет!!!",
        )
    }
}


class UpdateButtonContext<T : ChatContext, D>(
    bot: Bot,
    update: Update,
    val btnData: D,
) : UpdateContext<T>(bot, update)
