package com.github.kotlintelegrambot.extensions.filters

import anyAudio
import anyChat
import anyContact
import anyInvoice
import anyLocation
import anyMessage
import anyPhotoSize
import anySticker
import anyUpdate
import anyUser
import anyVideo
import anyVideoNote
import com.github.kotlintelegrambot.entities.Update
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

private const val ANY_DATE = 325232423
private const val ANY_CHAT_ID = 3523523L
private const val ANY_OTHER_CHAT_ID = 2213232L
private const val ANY_USER_ID = 235235235L
private const val ANY_OTHER_USER_ID = 7774575L

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FiltersTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideFiltersTestCases")
    fun `Test case - `(
        @Suppress("UNUSED_PARAMETER") testCaseName: String,
        filter: Filter,
        update: Update,
        expectedFilterResult: Boolean
    ) {
        val filterResult = filter.checkFor(update = update)

        Assertions.assertEquals(expectedFilterResult, filterResult)
    }

    fun provideFiltersTestCases(): Stream<Arguments> = Stream.of(
        buildTestCase(
            testCaseName = "AND function with all filters returning true returns true",
            filter = anyFilterReturning(true)
                and anyFilterReturning(true)
                and anyFilterReturning(true),
            update = anyUpdate(message = anyMessage()),
            expectedFilterResult = true
        ),
        buildTestCase(
            testCaseName = "AND function with one filter returning false returns false",
            filter = anyFilterReturning(false)
                and anyFilterReturning(true)
                and anyFilterReturning(true),
            update = anyUpdate(message = anyMessage()),
            expectedFilterResult = false
        ),
        buildTestCase(
            testCaseName = "OR function with one filter returning true returns true",
            filter = anyFilterReturning(true)
                or anyFilterReturning(false)
                or anyFilterReturning(false),
            update = anyUpdate(message = anyMessage()),
            expectedFilterResult = true
        ),
        buildTestCase(
            testCaseName = "OR function with all filters returning false returns false",
            filter = anyFilterReturning(false)
                or anyFilterReturning(false)
                or anyFilterReturning(false),
            update = anyUpdate(message = anyMessage()),
            expectedFilterResult = false
        ),
        buildTestCase(
            testCaseName = "NOT operator returns false for a filter returning true",
            filter = !anyFilterReturning(true),
            update = anyUpdate(message = anyMessage()),
            expectedFilterResult = false
        ),
        buildTestCase(
            testCaseName = "NOT operator returns true for a filter returning false",
            filter = !anyFilterReturning(false),
            update = anyUpdate(message = anyMessage()),
            expectedFilterResult = true
        ),
        buildTestCase(
            testCaseName = "Filter.Custom returns true if the custom predicate returns true",
            filter = Filter.Custom(customPredicate = { true }),
            update = anyUpdate(message = anyMessage()),
            expectedFilterResult = true
        ),
        buildTestCase(
            testCaseName = "Filter.Custom returns false if the custom predicate returns false",
            filter = Filter.Custom(customPredicate = { false }),
            update = anyUpdate(message = anyMessage()),
            expectedFilterResult = false
        ),
        buildTestCase(
            testCaseName = "Filter.All returns true for any message",
            filter = Filter.All,
            update = anyUpdate(message = anyMessage()),
            expectedFilterResult = true
        ),
        buildTestCase(
            testCaseName = "Filter.Text returns false for a non text message",
            filter = Filter.Text,
            update = anyUpdate(message = anyMessage(text = null)),
            expectedFilterResult = false
        ),
        buildTestCase(
            testCaseName = "Filter.Text returns true for a non command text message",
            filter = Filter.Text,
            update = anyUpdate(message = anyMessage(text = "Hello ruka world!!")),
            expectedFilterResult = true
        ),
        buildTestCase(
            testCaseName = "Filter.Text returns false for a command text message",
            filter = Filter.Text,
            update = anyUpdate(message = anyMessage(text = "/help")),
            expectedFilterResult = false
        ),
        buildTestCase(
            testCaseName = "Filter.Command returns false for a non text message",
            filter = Filter.Command,
            update = anyUpdate(message = anyMessage(text = null)),
            expectedFilterResult = false
        ),
        buildTestCase(
            testCaseName = "Filter.Command returns false for a non command text message",
            filter = Filter.Command,
            update = anyUpdate(message = anyMessage(text = "Hello ruka world!!")),
            expectedFilterResult = false
        ),
        buildTestCase(
            testCaseName = "Filter.Command returns true for a command text message",
            filter = Filter.Command,
            update = anyUpdate(message = anyMessage(text = "/help")),
            expectedFilterResult = true
        ),
        buildTestCase(
            testCaseName = "Filter.Reply returns false for a non reply message",
            filter = Filter.Reply,
            update = anyUpdate(message = anyMessage(replyToMessage = null)),
            expectedFilterResult = false
        ),
        buildTestCase(
            testCaseName = "Filter.Reply returns true for a reply message",
            filter = Filter.Reply,
            update = anyUpdate(message = anyMessage(replyToMessage = anyMessage())),
            expectedFilterResult = true
        ),
        buildTestCase(
            testCaseName = "Filter.Forward returns false for a non forwarded message",
            filter = Filter.Forward,
            update = anyUpdate(message = anyMessage(forwardDate = null)),
            expectedFilterResult = false
        ),
        buildTestCase(
            testCaseName = "Filter.Forward returns true for a forwarded message",
            filter = Filter.Forward,
            update = anyUpdate(message = anyMessage(forwardDate = ANY_DATE)),
            expectedFilterResult = true
        ),
        buildTestCase(
            testCaseName = "Filter.Audio returns false for a non audio message",
            filter = Filter.Audio,
            update = anyUpdate(message = anyMessage(audio = null)),
            expectedFilterResult = false
        ),
        buildTestCase(
            testCaseName = "Filter.Audio returns true for an audio message",
            filter = Filter.Audio,
            update = anyUpdate(message = anyMessage(audio = anyAudio())),
            expectedFilterResult = true
        ),
        buildTestCase(
            testCaseName = "Filter.Photo returns false for a non photo message",
            filter = Filter.Photo,
            update = anyUpdate(message = anyMessage(photo = null)),
            expectedFilterResult = false
        ),
        buildTestCase(
            testCaseName = "Filter.Photo returns true for a photo message",
            filter = Filter.Photo,
            update = anyUpdate(message = anyMessage(photo = listOf(anyPhotoSize()))),
            expectedFilterResult = true
        ),
        buildTestCase(
            testCaseName = "Filter.Sticker returns false for a non sticker message",
            filter = Filter.Sticker,
            update = anyUpdate(message = anyMessage(sticker = null)),
            expectedFilterResult = false
        ),
        buildTestCase(
            testCaseName = "Filter.Sticker returns true for a sticker message",
            filter = Filter.Sticker,
            update = anyUpdate(message = anyMessage(sticker = anySticker())),
            expectedFilterResult = true
        ),
        buildTestCase(
            testCaseName = "Filter.Video returns false for a non video message",
            filter = Filter.Video,
            update = anyUpdate(message = anyMessage(video = null)),
            expectedFilterResult = false
        ),
        buildTestCase(
            testCaseName = "Filter.Video returns true for a video message",
            filter = Filter.Video,
            update = anyUpdate(message = anyMessage(video = anyVideo())),
            expectedFilterResult = true
        ),
        buildTestCase(
            testCaseName = "Filter.VideoNote returns false for a non video note message",
            filter = Filter.VideoNote,
            update = anyUpdate(message = anyMessage(videoNote = null)),
            expectedFilterResult = false
        ),
        buildTestCase(
            testCaseName = "Filter.VideoNote returns true for a video note message",
            filter = Filter.VideoNote,
            update = anyUpdate(message = anyMessage(videoNote = anyVideoNote())),
            expectedFilterResult = true
        ),
        buildTestCase(
            testCaseName = "Filter.Location returns false for a non location message",
            filter = Filter.Location,
            update = anyUpdate(message = anyMessage(location = null)),
            expectedFilterResult = false
        ),
        buildTestCase(
            testCaseName = "Filter.Location returns true for a location message",
            filter = Filter.Location,
            update = anyUpdate(message = anyMessage(location = anyLocation())),
            expectedFilterResult = true
        ),
        buildTestCase(
            testCaseName = "Filter.Contact returns false for a non contact message",
            filter = Filter.Contact,
            update = anyUpdate(message = anyMessage(contact = null)),
            expectedFilterResult = false
        ),
        buildTestCase(
            testCaseName = "Filter.Contact returns true for a contact message",
            filter = Filter.Contact,
            update = anyUpdate(message = anyMessage(contact = anyContact())),
            expectedFilterResult = true
        ),
        buildTestCase(
            testCaseName = "Filter.Invoice returns false for a non invoice message",
            filter = Filter.Invoice,
            update = anyUpdate(message = anyMessage(invoice = null)),
            expectedFilterResult = false
        ),
        buildTestCase(
            testCaseName = "Filter.Invoice returns true for an invoice message",
            filter = Filter.Invoice,
            update = anyUpdate(message = anyMessage(invoice = anyInvoice())),
            expectedFilterResult = true
        ),
        buildTestCase(
            testCaseName = "Filter.Chat returns true if the message belongs to the indicated chat id",
            filter = Filter.Chat(ANY_CHAT_ID),
            update = anyUpdate(message = anyMessage(chat = anyChat(id = ANY_CHAT_ID))),
            expectedFilterResult = true
        ),
        buildTestCase(
            testCaseName = "Filter.Chat returns false if the message doesn't belong to the indicated chat id",
            filter = Filter.Chat(ANY_CHAT_ID),
            update = anyUpdate(message = anyMessage(chat = anyChat(id = ANY_OTHER_CHAT_ID))),
            expectedFilterResult = false
        ),
        buildTestCase(
            testCaseName = "Filter.User returns true if the message was sent from the indicated user id",
            filter = Filter.User(ANY_USER_ID),
            update = anyUpdate(message = anyMessage(from = anyUser(userId = ANY_USER_ID))),
            expectedFilterResult = true
        ),
        buildTestCase(
            testCaseName = "Filter.User returns false if the message wasn't sent from the indicated user id",
            filter = Filter.User(ANY_USER_ID),
            update = anyUpdate(message = anyMessage(from = anyUser(userId = ANY_OTHER_USER_ID))),
            expectedFilterResult = false
        ),
        buildTestCase(
            testCaseName = "Filter.User returns false if there is no message sender",
            filter = Filter.User(ANY_USER_ID),
            update = anyUpdate(message = anyMessage(from = null)),
            expectedFilterResult = false
        ),
        buildTestCase(
            testCaseName = "Filter.Private returns true if the chat type is 'private'",
            filter = Filter.Private,
            update = anyUpdate(message = anyMessage(chat = anyChat(type = "private"))),
            expectedFilterResult = true
        ),
        buildTestCase(
            testCaseName = "Filter.Private returns false if the chat type is not 'private'",
            filter = Filter.Private,
            update = anyUpdate(message = anyMessage(chat = anyChat(type = "group"))),
            expectedFilterResult = false
        ),
        buildTestCase(
            testCaseName = "Filter.Group returns true if the chat type is 'group'",
            filter = Filter.Group,
            update = anyUpdate(message = anyMessage(chat = anyChat(type = "group"))),
            expectedFilterResult = true
        ),
        buildTestCase(
            testCaseName = "Filter.Group returns true if the chat type is 'supergroup'",
            filter = Filter.Group,
            update = anyUpdate(message = anyMessage(chat = anyChat(type = "supergroup"))),
            expectedFilterResult = true
        ),
        buildTestCase(
            testCaseName = "Filter.Group returns false if the chat type is not 'group' or 'supergroup'",
            filter = Filter.Group,
            update = anyUpdate(message = anyMessage(chat = anyChat(type = "private"))),
            expectedFilterResult = false
        ),
        buildTestCase(
            testCaseName = "Filter.Channel returns true if the chat type is 'channel'",
            filter = Filter.Channel,
            update = anyUpdate(message = anyMessage(chat = anyChat(type = "channel"))),
            expectedFilterResult = true
        ),
        buildTestCase(
            testCaseName = "Filter.Channel returns false if the chat type is not 'channel'",
            filter = Filter.Channel,
            update = anyUpdate(message = anyMessage(chat = anyChat(type = "private"))),
            expectedFilterResult = false
        )
    )

    private fun anyFilterReturning(valueToReturn: Boolean): Filter = object : Filter {
        override fun Update.predicate(): Boolean = valueToReturn
    }

    private fun buildTestCase(
        testCaseName: String,
        filter: Filter,
        update: Update,
        expectedFilterResult: Boolean
    ): Arguments = Arguments.of(testCaseName, filter, update, expectedFilterResult)
}
