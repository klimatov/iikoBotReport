package domain.models

class MessageParam(
    val reportResult: ReportResult,
    val oldReport: ReportResult,
    val sendChatId: Long, // ID чата/юзера куда будет отправлятся отчет
    val messageHeader: Boolean, // отображать ли заголовок в отчете?
    val messageSuffix: Map<Int, String>, // суфикс руб./шт. в колонке номер Int
    val messageAmount: Int, // доп. строка с суммой колонки номер Int (0 если не выводим)
    val messageWordLimit: Map<Int, Int> = mapOf() // в колонке номер Int количество слов не более Int
)