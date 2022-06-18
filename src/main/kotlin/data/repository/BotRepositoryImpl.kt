package data.repository

import SecurityData.TELEGRAM_CHAT_ID
import core.Bot
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.types.ChatId
import domain.repository.BotRepository

class BotRepositoryImpl(private val bot: Bot) :BotRepository {
    private val targetChatId = ChatId(TELEGRAM_CHAT_ID)

    override suspend fun sendMessageToChat(text: String): Boolean {
        try {
            bot.bot.sendTextMessage(targetChatId, text)
            return true
        } catch (e: Exception) {
            println(e)
            return false
        }


    }
}