package com.github.kotlintelegrambot.dispatcher.new

import com.github.kotlintelegrambot.entities.*

data class Redirection(
    val command: String? = null,
    val buttonData: String? = null,
)

fun buildRedirectedUpdate(update: Update): Update {

    val redirection = update.redirectAndClear()

    var newUpdate: Update? = null
    val newMessage =
        update.message ?: update.callbackQuery?.message ?: Message(
            text = "/${redirection.command}",
            from = update.from,
            chat = update.chat,
            date = System.currentTimeMillis(),
            messageId = update.messageId ?: 0,
        ).copy(
            text = "/${redirection.command}",
            from = update.from,
            chat = update.chat
        )

    if (redirection.command != null) {
        newUpdate = update.copy(
            message = newMessage,
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
                message = newMessage,
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
    newUpdate.redirectionPrefix(update.redirectionPrefix())
    newUpdate.withChatContextSource(update.chatContextSource())
    return newUpdate
}

