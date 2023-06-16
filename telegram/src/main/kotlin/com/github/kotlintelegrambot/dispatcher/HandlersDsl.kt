package com.github.kotlintelegrambot.dispatcher

import com.github.kotlintelegrambot.dispatcher.handlers.*
import com.github.kotlintelegrambot.dispatcher.handlers.CallbackQueryHandler
import com.github.kotlintelegrambot.dispatcher.handlers.ChannelHandler
import com.github.kotlintelegrambot.dispatcher.handlers.CommandHandler
import com.github.kotlintelegrambot.dispatcher.handlers.ContactHandler
import com.github.kotlintelegrambot.dispatcher.handlers.DiceHandler
import com.github.kotlintelegrambot.dispatcher.handlers.InlineQueryHandler
import com.github.kotlintelegrambot.dispatcher.handlers.LocationHandler
import com.github.kotlintelegrambot.dispatcher.handlers.MessageHandler
import com.github.kotlintelegrambot.dispatcher.handlers.NewChatMembersHandler
import com.github.kotlintelegrambot.dispatcher.handlers.PollAnswerHandler
import com.github.kotlintelegrambot.dispatcher.handlers.PreCheckoutQueryHandler
import com.github.kotlintelegrambot.dispatcher.handlers.TextHandler
import com.github.kotlintelegrambot.dispatcher.handlers.media.AnimationHandler
import com.github.kotlintelegrambot.dispatcher.handlers.media.AudioHandler
import com.github.kotlintelegrambot.dispatcher.handlers.media.DocumentHandler
import com.github.kotlintelegrambot.dispatcher.handlers.media.GameHandler
import com.github.kotlintelegrambot.dispatcher.handlers.media.PhotosHandler
import com.github.kotlintelegrambot.dispatcher.handlers.media.StickerHandler
import com.github.kotlintelegrambot.dispatcher.handlers.media.VideoHandler
import com.github.kotlintelegrambot.dispatcher.handlers.media.VideoNoteHandler
import com.github.kotlintelegrambot.dispatcher.handlers.media.VoiceHandler
import com.github.kotlintelegrambot.dispatcher.new.Action
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.extensions.filters.Filter
import com.github.kotlintelegrambot.extensions.filters.Filter.All

fun HandleCollector<*>.message(handleMessage: HandleMessage) {
    addHandler(MessageHandler(All, handleMessage))
}

fun HandleCollector<*>.message(filter: Filter, handleMessage: HandleMessage) {
    addHandler(MessageHandler(filter, handleMessage))
}

fun HandleCollector<*>.command(command: String, handleCommand: HandleCommand) {
    addHandler(CommandHandler(command, handleCommand))
}

fun HandleCollector<*>.text(text: String? = null, handleText: HandleText) {
    addHandler(TextHandler(text, handleText))
}

fun HandleCollector<*>.callbackQuery(data: String? = null, handleCallbackQuery: HandleCallbackQuery) {
    addHandler(CallbackQueryHandler(callbackData = data, handleCallbackQuery = handleCallbackQuery))
}

fun HandleCollector<*>.callbackQuery(
    callbackData: String? = null,
    callbackAnswerText: String? = null,
    callbackAnswerShowAlert: Boolean? = null,
    callbackAnswerUrl: String? = null,
    callbackAnswerCacheTime: Int? = null,
    handleCallbackQuery: HandleCallbackQuery
) {
    addHandler(
        CallbackQueryHandler(
            callbackData = callbackData,
            callbackAnswerText = callbackAnswerText,
            callbackAnswerShowAlert = callbackAnswerShowAlert,
            callbackAnswerUrl = callbackAnswerUrl,
            callbackAnswerCacheTime = callbackAnswerCacheTime,
            handleCallbackQuery = handleCallbackQuery
        )
    )
}

fun HandleCollector<*>.contact(handleContact: HandleContact) {
    addHandler(ContactHandler(handleContact))
}

fun HandleCollector<*>.location(handleLocation: HandleLocation) {
    addHandler(LocationHandler(handleLocation))
}

fun HandleCollector<*>.telegramError(handleError: HandleError) {
    addErrorHandler(ErrorHandler(handleError))
}

fun HandleCollector<*>.preCheckoutQuery(body: HandlePreCheckoutQuery) {
    addHandler(PreCheckoutQueryHandler(body))
}

fun HandleCollector<*>.channel(body: HandleChannelPost) {
    addHandler(ChannelHandler(body))
}

fun HandleCollector<*>.inlineQuery(body: HandleInlineQuery) {
    addHandler(InlineQueryHandler(body))
}

fun HandleCollector<*>.audio(body: HandleAudio) {
    addHandler(AudioHandler(body))
}

fun HandleCollector<*>.document(body: HandleDocument) {
    addHandler(DocumentHandler(body))
}

fun HandleCollector<*>.animation(body: HandleAnimation) {
    addHandler(AnimationHandler(body))
}

fun HandleCollector<*>.game(body: HandleGame) {
    addHandler(GameHandler(body))
}

fun HandleCollector<*>.photos(body: HandlePhotos) {
    addHandler(PhotosHandler(body))
}

fun HandleCollector<*>.sticker(body: HandleSticker) {
    addHandler(StickerHandler(body))
}

fun HandleCollector<*>.video(body: HandleVideo) {
    addHandler(VideoHandler(body))
}

fun HandleCollector<*>.voice(body: HandleVoice) {
    addHandler(VoiceHandler(body))
}

fun HandleCollector<*>.videoNote(body: HandleVideoNote) {
    addHandler(VideoNoteHandler(body))
}

fun HandleCollector<*>.newChatMembers(body: HandleNewChatMembers) {
    addHandler(NewChatMembersHandler(body))
}

fun HandleCollector<*>.pollAnswer(body: HandlePollAnswer) {
    addHandler(PollAnswerHandler(body))
}

fun HandleCollector<*>.dice(body: HandleDice) {
    addHandler(DiceHandler(body))
}


fun <T : ChatContext> HandleCollector<T>.update(filter: Filter, handleMessage: HandleUpdate<T>) {
    addHandler(CommonHandler({
        filter.checkFor(this)
    }, handleMessage))
}

fun <C : ChatContext, T> HandleCollector<C>.onButton(
    action: Action<T>,
    handleCommand: HandleButtonUpdate<C, T>
) {
    val filter = { update: Update ->
        when (val data = update.callbackQuery?.data) {
            null -> false
            else -> data.startsWith(action.actionCode, ignoreCase = true)
        }
    }
    addHandler(CommonButtonHandler(action, filter, handleCommand))
}


