package com.github.kotlintelegrambot.dispatcher

import com.github.kotlintelegrambot.entities.Update

interface ChatContextProvider {
    fun getChatContext(update: Update): ChatContext
    fun setChatContext(update: Update, chatContext: ChatContext)

}

interface ChatContextSource {

    fun <R : ChatContext> setChatContext(data: R)
    fun <T : ChatContext> getChatContext(): T
    fun flush()
}

interface ChatContext {
    val chatState: String
}

class EmptyChatContextSource : ChatContextSource {
    override fun <R : ChatContext> setChatContext(data: R) {
        throw IllegalStateException("Chat data source is not set")
    }

    override fun <T : ChatContext> getChatContext(): T {
        throw IllegalStateException("Chat data source is not set")
    }

    override fun flush() {
        throw IllegalStateException("Chat state source is not set")
    }
}

@Suppress("UNCHECKED_CAST")
fun wrapAsChatContextSource(update: Update, chatContextProvider: ChatContextProvider) =
    object : ChatContextSource {
        private var chatData: ChatContext? = null

        override fun <T : ChatContext> getChatContext(): T {
            chatData = chatData ?: chatContextProvider.getChatContext(update)
            return chatData as T
        }

        override fun flush() {
            if (chatData != null) {
                chatContextProvider.setChatContext(update, chatData!!)
            }
        }

        override fun <T : ChatContext> setChatContext(data: T) {
            chatData = data
        }


    }

