package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.dispatcher.ChatContextSource
import com.github.kotlintelegrambot.dispatcher.EmptyChatContextSource
import com.github.kotlintelegrambot.dispatcher.new.Redirection
import com.github.kotlintelegrambot.entities.payments.PreCheckoutQuery
import com.github.kotlintelegrambot.entities.payments.ShippingQuery
import com.github.kotlintelegrambot.entities.polls.Poll
import com.github.kotlintelegrambot.entities.polls.PollAnswer
import com.github.kotlintelegrambot.types.ConsumableObject
import com.github.kotlintelegrambot.types.DispatchableObject
import com.google.gson.annotations.SerializedName as Name

data class Update constructor(
    @Name("update_id") val updateId: Long,
    val message: Message? = null,
    @Name("edited_message") val editedMessage: Message? = null,
    @Name("channel_post") val channelPost: Message? = null,
    @Name("edited_channel_post") val editedChannelPost: Message? = null,
    @Name("inline_query") val inlineQuery: InlineQuery? = null,
    @Name("chosen_inline_result") val chosenInlineResult: ChosenInlineResult? = null,
    @Name("callback_query") val callbackQuery: CallbackQuery? = null,
    @Name("shipping_query") val shippingQuery: ShippingQuery? = null,
    @Name("pre_checkout_query") val preCheckoutQuery: PreCheckoutQuery? = null,
    @Name("poll") val poll: Poll? = null,
    @Name("poll_answer") val pollAnswer: PollAnswer? = null,
    @Name("my_chat_member") val myChatMember: ChatMemberUpdated? = null,
) : DispatchableObject, ConsumableObject() {
    private var chatContextSource: ChatContextSource = EmptyChatContextSource()
    private var redirection: Redirection? = null
    private var redirected: Boolean = false
    private var redirectinPrefix: String = ""

    fun withChatContextSource(contextSource: ChatContextSource): Update {
        this.chatContextSource = contextSource
        return this
    }

    fun chatContextSource(): ChatContextSource {
        return chatContextSource
    }

    fun redirectTo(command: String? = null, buttonData: String? = null) {
        redirection = Redirection(
            command = command,
            buttonData = buttonData
        )
    }

    fun hasRedirection(): Boolean {
        if (redirected) {
            return false
        }
        return redirection != null
    }

    fun redirectAndClear(): Redirection {
        val redirection = this.redirection
        this.redirection = null
        this.redirected = true
        return redirection ?: Redirection()
    }

    fun redirectionPrefix(prefix: String) {
        if (prefix == null) {
            redirectinPrefix = ""
        }
        redirectinPrefix = prefix
    }

    fun redirectionPrefix(): String {
        if (redirectinPrefix == null) {
            return ""
        }
        return redirectinPrefix
    }

}

data class ChatMemberUpdated constructor(
    @Name("chat") val chat: Chat,
    @Name("from") val from: User,
    @Name("date") val date: Int,
    @Name("old_chat_member") val oldChatMember: ChatMember,
    @Name("new_chat_member") val newChatMember: ChatMember,
    @Name("invite_link") val inviteLink: ChatInviteLink? = null,
) : DispatchableObject, ConsumableObject()

data class ChatInviteLink constructor(
    @Name("invite_link") val inviteLink: String,
    @Name("creator") val creator: User,
    @Name("is_primary") val isPrimary: Boolean,
    @Name("is_revoked") val isRevoked: Boolean,
    @Name("expire_date") val expireDate: Int? = null,
    @Name("member_limit") val memberLimit: Int? = null,
) : DispatchableObject, ConsumableObject()

/**
 * Generate list of key-value from start payload.
 * For more info {@link https://core.telegram.org/bots#deep-linking}
 */
fun Update.getStartPayload(delimiter: String = "-"): List<Pair<String, String>> {
    return message?.let {
        val parameters = it.text?.substringAfter("start ", "")
        if (parameters == null || parameters.isEmpty()) {
            return emptyList()
        }

        val split = parameters.split("&")
        split.map {
            val keyValue = it.split(delimiter)
            Pair(keyValue[0], keyValue[1])
        }
    } ?: emptyList()
}

val Update.fromId
    get() = ChatId.fromId(from.id)

val Update.messageId
    get() =
        message?.messageId
            ?: editedMessage?.messageId
            ?: channelPost?.messageId
            ?: editedChannelPost?.messageId
            ?: callbackQuery?.message?.messageId

val Update.text
    get() = message?.text
        ?: editedMessage?.text
        ?: channelPost?.text
        ?: editedChannelPost?.text
        ?: pollAnswer?.pollId

val Update.fromUsername
    get() = from.username

val Update.from
    get() =
        message?.from
            ?: editedMessage?.from
            ?: channelPost?.from
            ?: editedChannelPost?.from
            ?: inlineQuery?.from
            ?: chosenInlineResult?.from
            ?: callbackQuery?.from
            ?: shippingQuery?.from
            ?: preCheckoutQuery?.from
            ?: pollAnswer?.user
            ?: throw IllegalStateException("Update has no user id, update: $this")

val Update.chatId
    get() = ChatId.fromId(chat.id)

val Update.chat
    get() =
        message?.chat
            ?: editedMessage?.chat
            ?: channelPost?.chat
            ?: editedChannelPost?.chat
            ?: callbackQuery?.message?.chat
            ?: myChatMember?.chat
            ?: throw IllegalStateException("Update has no chat id, update: $this")


val Update.isPrivateChat
    get() = message?.chat?.type == InlineQuery.ChatType.PRIVATE.name.lowercase()
        || editedMessage?.chat?.type == InlineQuery.ChatType.PRIVATE.name.lowercase()
        || channelPost?.chat?.type == InlineQuery.ChatType.PRIVATE.name.lowercase()
        || editedChannelPost?.chat?.type == InlineQuery.ChatType.PRIVATE.name.lowercase()
        || inlineQuery?.from?.id != null
        || chosenInlineResult?.from?.id != null
        || callbackQuery?.from?.id != null
        || shippingQuery?.from?.id != null
        || preCheckoutQuery?.from?.id != null
        || pollAnswer?.user?.id != null
