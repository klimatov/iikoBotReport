package domain.usecases

import domain.models.MessageParam
import domain.models.ReportResult
import domain.repository.BotRepository

class SendReportMessage(private val botRepository: BotRepository) {

    suspend fun execute(messageParam: MessageParam): Boolean {
        val messageText = FormatText().report(messageParam)
        if (messageText.isNotEmpty()) {
            return botRepository.sendMessageToChat(text = messageText, sendChatId = messageParam.sendChatId)
        } else return false
    }
}