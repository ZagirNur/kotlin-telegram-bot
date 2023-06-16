package com.github.kotlintelegrambot.dispatcher.new

import com.github.kotlintelegrambot.dispatcher.ChatContext
import com.github.kotlintelegrambot.dispatcher.ChatContextProvider
import com.github.kotlintelegrambot.entities.Update

class EmptyChatContextProvider : ChatContextProvider {

    override fun getChatContext(update: Update): ChatContext {
        TODO("Not yet implemented")
    }

    override fun setChatContext(update: Update, chatContext: ChatContext) {
        TODO("Not yet implemented")
    }
}
