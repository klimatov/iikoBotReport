package domain.usecases

import domain.models.ReportResult
import domain.repository.BotRepository

class SendReportMessage(private val botRepository: BotRepository) {

    suspend fun execute(reportResult: ReportResult): Boolean {
        botRepository.sendMessageToChat(FormatText().report(reportResult))
        return true
    }
}