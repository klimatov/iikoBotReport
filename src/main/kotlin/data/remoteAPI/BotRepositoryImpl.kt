package data.remoteAPI

import core.Bot
import dev.inmo.tgbotapi.extensions.api.send.media.sendPhoto
import dev.inmo.tgbotapi.extensions.api.send.media.sendVisualMediaGroup
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.requests.abstracts.FileUrl
import dev.inmo.tgbotapi.types.ChatId
import dev.inmo.tgbotapi.types.media.TelegramMediaPhoto
import domain.repository.BotRepository
import utils.Logging

class BotRepositoryImpl(private val bot: Bot) : BotRepository {
    private val tag = this::class.java.simpleName
    override suspend fun sendMessageToChat(text: String, sendChatId: Long, photosList: List<String>): Boolean {
        try {
            val cropText = text.take(4095)
            when (photosList.size) {
                0 -> bot.bot.sendTextMessage(ChatId(sendChatId), cropText)
                1 -> bot.bot.sendPhoto(
                    ChatId(sendChatId),
                    FileUrl(photosList.first()),
                    if (cropText.length <= 1000) cropText else null
                )

                else -> {
                    val photos = mutableListOf<TelegramMediaPhoto>()
                    photosList.forEachIndexed { index, imageUrl ->
                        photos.add(
                            TelegramMediaPhoto(
                                FileUrl(
                                    imageUrl
                                ),
                                if ((index == 0)&&(cropText.length <= 1000)) cropText else null
                            )
                        )
                    }
                    bot.bot.sendVisualMediaGroup(ChatId(sendChatId), photos)
                }
            }
            if (cropText.length > 1000) bot.bot.sendTextMessage(ChatId(sendChatId), cropText)

            return true
        } catch (e: Exception) {
            Logging.e(tag, e.toString())
            return false
        }


    }
}