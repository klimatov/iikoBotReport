package domain.usecases

import domain.models.ReportResult
import domain.repository.BotRepository
import domain.repository.ReportRepository

class MakeReportPostUseCase(private val reportRepository: ReportRepository, private val botRepository: BotRepository) {
    private var oldReport = ReportResult(null)
    suspend fun execute() {

        // тут получаем отчет
        val result = GetReport(reportRepository = reportRepository).execute()

        // сравниваем с прошлым
        if (result.table != oldReport.table) {
            // если изменился, то отправляем в чат
            SendReportMessage(botRepository = botRepository).execute(reportResult = result, oldReport = oldReport)
            oldReport.table = result.table
        } else println("Ничего не изменилось!")
    }
}