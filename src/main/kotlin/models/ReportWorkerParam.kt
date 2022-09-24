package models

data class ReportWorkerParam(
    val workerId: String, // ID отчета (worker'а)
    val workerName: String = "", // название отчета
    val reportId: String = "", // ID отчета в iiko
    val reportPeriod: Int = 0, // период данных для формирования отчета из iiko
    // (0 - сегодня, n - количество дней, -1 с начала недели, -2 с начала месяца, -3 с начала квартала, -4 с начала года)
    val sendChatId: Long = 0, // ID чата/юзера куда будет отправляться отчет
    val sendWhenType: Int = 1, // когда отправлять отчет: 1 - периодически, 2 - дни недели, 3 - числа месяца, 0 - ежедневно
    val sendPeriod: Int = 1, // период отправки в минутах
    val sendTime: List<String> = listOf("10:00"), // время отправки (для еженедельного/ежемесячного отчета)
    val sendWeekDay: List<Int> = listOf(1), // дни недели для отправки отчета
    val sendMonthDay: List<Int> = listOf(1), // числа месяца для отправки отчета (32 - последний день месяца)
    val messageHeader: Boolean = true, // отображать ли заголовок в отчете?
    val messageSuffix: Map<Int, String> = mapOf(), // суфикс руб./шт. в колонке номер Int
    val messageAmount: Int = 0, // доп. строка с суммой колонки номер Int (0 если не выводим)
    val messageWordLimit: Map<Int, Int> = mapOf(), // в колонке номер Int количество слов не более Int
    val nameInHeader: Boolean = false, // Выводить название отчета в заголовке сообщения
    val workerIsActive: Boolean = true // Галка активности отчета
)