package domain.usecases

import domain.models.MessageParam
import domain.models.ReportParam
import domain.models.RequestParam
import domain.models.ReportResult
import domain.repository.BotRepository
import domain.repository.ReportRepository
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import utils.Logging
class MakeReportPostUseCase(private val reportRepository: ReportRepository, private val botRepository: BotRepository) {
    private val tag = this::class.java.simpleName
    private var oldReport = ReportResult(null)
    suspend fun execute(reportParam: ReportParam) {

        // тут получаем отчет
        val requestParam = mapToRequestParam(reportParam)
        val result = GetReport(reportRepository = reportRepository).execute(requestParam)

        // сравниваем с прошлым
        if (result.table != oldReport.table) {
            // если изменился, то отправляем в чат
            val messageParam: MessageParam = MessageParam(
                reportResult = result,
                oldReport = oldReport,
                sendChatId = reportParam.sendChatId,
                messageHeader = reportParam.messageHeader,
                messageSuffix = reportParam.messageSuffix,
                messageAmount = reportParam.messageAmount,
                messageWordLimit = reportParam.messageWordLimit,
                nameInHeader = reportParam.nameInHeader,
                workerName = reportParam.workerName
            )
            val sendResult = SendReportMessage(botRepository = botRepository).execute(messageParam)
            oldReport.table = result.table
            Logging.i(tag,"Данные изменились, ${if (sendResult) "отправлены в чат" else "отправить в чат НЕ УДАЛОСЬ"}...")
        } else Logging.i(tag,"Данные не изменились...")
    }

    private fun mapToRequestParam(reportParam: ReportParam): RequestParam {

        val reportPeriod = reportParam.reportPeriod // период данных для формирования отчета из iiko
        val correctionNight = if (LocalDateTime.now().hour <= 3) 1 else 0 // с 0 до 3 часов используем вчерашнюю дату
        val dateNow = LocalDate.now()

        val dateFrom = when (reportPeriod) {
            -1 -> dateNow.minusDays(dateNow.dayOfWeek.value.toLong() - 1)
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) // -1 с начала недели

            -2 -> LocalDate.of(dateNow.year, dateNow.month, 1)
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) // -2 с начала месяца

            -3 -> LocalDate.of(dateNow.year, ((dateNow.monthValue - 1) / 3 + 1) * 3 - 2, 1)
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))// -3 с начала квартала

            -4 -> LocalDate.of(dateNow.year, 1, 1)
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) // -4 с начала года

            else -> dateNow // in 0..Integer.MAX_VALUE
                .minusDays((reportPeriod + correctionNight).toLong())
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) // 0 - сегодня, n - количество дней
        }

        return RequestParam(
            reportId = reportParam.reportId,
            dateFrom = dateFrom,
            dateTo = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        )
    }
}