package domain.usecases

import domain.models.MessageParam
import domain.models.ReportResult
import domain.repository.BotRepository

class SendReportMessage(private val botRepository: BotRepository) {

    suspend fun execute(messageParam: MessageParam): Boolean {
        botRepository.sendMessageToChat(text = FormatText().report(messageParam), sendChatId = messageParam.sendChatId)
        return true
    }
}