package com.github.kotlintelegrambot.extensions.filters

import com.github.kotlintelegrambot.dispatcher.ChatContext
import com.github.kotlintelegrambot.entities.*

interface Filter {
    fun checkFor(update: Update): Boolean = update.predicate()
    fun Update.predicate(): Boolean = false


    infix fun and(otherFilter: Filter): Filter = object : Filter {
        override fun Update.predicate(): Boolean =
            this@Filter.checkFor(this) && otherFilter.checkFor(this)
    }

    infix fun or(otherFilter: Filter): Filter = object : Filter {
        override fun Update.predicate(): Boolean =
            this@Filter.checkFor(this) || otherFilter.checkFor(this)
    }

    operator fun not(): Filter = object : Filter {
        override fun Update.predicate(): Boolean = !this@Filter.checkFor(this)
    }

    class Custom(private val customPredicate: Update.() -> Boolean) : Filter {
        override fun Update.predicate(): Boolean = customPredicate()
    }

    open class ContextHas<T : ChatContext>(private val customPredicate: T.() -> Boolean) : Filter {
        override fun Update.predicate(): Boolean {
            val chatContext = chatContextSource().getChatContext<T>()
            return chatContext.customPredicate()
        }
    }

    object All : Filter {
        override fun Update.predicate(): Boolean = true
    }

    object Text : Filter {
        override fun Update.predicate(): Boolean = text != null && !(text?.startsWith("/")?:false)
    }

    class TextIs(private val text: String) : Filter {
        override fun Update.predicate(): Boolean = text != null
            && text == this@TextIs.text
    }

    object Command : Filter {
        override fun Update.predicate(): Boolean = text != null && text?.startsWith("/")?:false
    }

    class CommandIs(
        private val command: String
    ) : Filter {
        override fun Update.predicate(): Boolean {
            return text != null && text?.startsWith("/$command")?:false
        }
    }

    object Reply : Filter {
        override fun Update.predicate(): Boolean = message?.replyToMessage != null
    }

    object Forward : Filter {
        override fun Update.predicate(): Boolean = message?.forwardDate != null
    }

    object Audio : Filter {
        override fun Update.predicate(): Boolean = message?.audio != null
    }

    object Photo : Filter {
        override fun Update.predicate(): Boolean = message?.photo != null && message.photo.isNotEmpty()
    }

    object Sticker : Filter {
        override fun Update.predicate(): Boolean = message?.sticker != null
    }

    object Video : Filter {
        override fun Update.predicate(): Boolean = message?.video != null
    }

    object VideoNote : Filter {
        override fun Update.predicate(): Boolean = message?.videoNote != null
    }

    object Location : Filter {
        override fun Update.predicate(): Boolean = message?.location != null
    }

    object Contact : Filter {
        override fun Update.predicate(): Boolean = message?.contact != null
    }

    object Invoice : Filter {
        override fun Update.predicate(): Boolean = message?.invoice != null
    }

    object Button : Filter {
        override fun Update.predicate() = callbackQuery?.data != null
    }

    class Chat(private val chatId: Long) : Filter {
        override fun Update.predicate() = chatSafe?.id == this@Chat.chatId

    }

    class User(private val userId: Long) : Filter {
        override fun Update.predicate() = fromSafe?.id == this@User.userId
    }

    object Group : Filter {
        override fun Update.predicate() = chatSafe?.type == "group" || chatSafe?.type == "supergroup"
    }

    object Private : Filter {
        override fun Update.predicate() = chatSafe?.type == "private"
    }

    object Channel : Filter {
        override fun Update.predicate() = chatSafe?.type == "channel"

    }
}

private val Update.chatSafe
    get() = try {
        this.chat
    } catch (e: Exception) {
        null
    }

private val Update.fromSafe
    get() = try {
        this.from
    } catch (e: Exception) {
        null
    }
