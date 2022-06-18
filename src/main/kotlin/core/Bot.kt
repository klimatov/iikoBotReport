package core

import SecurityData.TELEGRAM_BOT_TOKEN
import SecurityData.TELEGRAM_CHAT_ID
import dev.inmo.tgbotapi.bot.ktor.telegramBot
import dev.inmo.tgbotapi.extensions.api.bot.getMe
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.types.ChatId
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class Bot(private val job: CompletableJob) {
    private val botToken = TELEGRAM_BOT_TOKEN
    val bot = telegramBot(botToken)

    suspend fun start() {
        val scope = CoroutineScope(Dispatchers.Default + job)
        bot.buildBehaviourWithLongPolling(scope) {
            onCommand("start"){
                sendTextMessage(it.chat, "Стартуешь?")
            }
            println("Bot started! ${getMe()}")
        }.start()
    }
}