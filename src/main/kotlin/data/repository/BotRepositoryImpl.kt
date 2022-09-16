package data.repository

import core.Bot
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.types.ChatId
import domain.repository.BotRepository
import utils.Logging
class BotRepositoryImpl(private val bot: Bot) :BotRepository {
    private val tag = this::class.java.simpleName
    override suspend fun sendMessageToChat(text: String, sendChatId: Long): Boolean {
        try {
            bot.bot.sendTextMessage(ChatId(sendChatId), text)
            return true
        } catch (e: Exception) {
            Logging.e(tag,e.toString())
            return false
        }


    }
}