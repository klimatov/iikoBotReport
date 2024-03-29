package domain.models

class MessageParam(
    val reportResult: ReportResult,
    val oldReport: ReportResult,
    val sendChatId: List<Long> = listOf(), // список ID чатов/юзеров куда будет отправляться отчет
    val messageHeader: Boolean, // отображать ли заголовок в отчете?
    val messageSuffix: Map<Int, String>, // суфикс руб./шт. в колонке номер Int
    val messageAmount: Int, // доп. строка с суммой колонки номер Int (0 если не выводим)
    val messageWordLimit: Map<Int, Int> = mapOf(), // в колонке номер Int количество слов не более Int
    val nameInHeader: Boolean = false, // Выводить название отчета в заголовке сообщения
    val workerName: String = "", // название отчета

)