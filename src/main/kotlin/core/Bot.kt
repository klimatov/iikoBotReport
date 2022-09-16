package core

import SecurityData.TELEGRAM_BOT_TOKEN
import dev.inmo.tgbotapi.bot.ktor.telegramBot
import dev.inmo.tgbotapi.extensions.api.bot.getMe
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import utils.Logging

class Bot(private val job: CompletableJob) {
    private val tag = this::class.java.simpleName
    private val botToken = TELEGRAM_BOT_TOKEN
    val bot = telegramBot(token = botToken)

    suspend fun start() {
        val scope = CoroutineScope(Dispatchers.Default + job)
        bot.buildBehaviourWithLongPolling(scope) {
            onCommand("start") {
                sendTextMessage(it.chat, "Стартуешь?")
            }
            Logging.i(tag,"Telegram Bot started! ${getMe()}")
        }.start()
    }
}