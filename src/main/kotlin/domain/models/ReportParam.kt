package domain.models

class ReportParam(
    val reportId: String, // ID отчета в iiko
    val reportPeriod: Int, // период данных для формирования отчета из iiko
    // (0 - сегодня, n - количество дней, -1 с начала недели, -2 с начала месяца, -3 с начала квартала, -4 с начала года)
    val sendChatId: Long, // ID чата/юзера куда будет отправлятся отчет
    val messageHeader: Boolean, // отображать ли заголовок в отчете?
    val messageSuffix: Map<Int, String>, // суфикс руб./шт. в колонке номер Int
    val messageAmount: Int // доп. строка с суммой колонки номер Int (0 если не выводим)
)