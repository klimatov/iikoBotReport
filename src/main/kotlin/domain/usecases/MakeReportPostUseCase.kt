package domain.usecases

import domain.models.ReportResult
import domain.repository.BotRepository
import domain.repository.ReportRepository

class MakeReportPostUseCase(private val reportRepository: ReportRepository, private val botRepository: BotRepository) {
    private var oldValue = ReportResult(null)
    suspend fun execute() {

        // тут получаем отчет
        val result = GetReport(reportRepository = reportRepository).execute()

        // сравниваем с прошлым
        if (result.table != oldValue.table) {
            oldValue.table = result.table

            // если изменился, то отправляем в чат
            SendReportMessage(botRepository = botRepository).execute(reportResult = result)

        } else println("Ничего не изменилось!")
    }
}