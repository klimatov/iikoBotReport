package models

data class WorkerParam(
    val workerId: String = "", // ID отчета (worker'а)
    val workerName: String = "", // название отчета
    val sendChatId: List<Long> = listOf(), // список ID чатов/юзеров куда будет отправляться отчет
    val sendWhenType: Int = 1, // когда отправлять отчет: 1 - периодически, 2 - дни недели, 3 - числа месяца, 0 - ежедневно, 4 - в даты
    val sendPeriod: Int = 1, // период отправки в минутах
    val sendTime: List<String> = listOf("10:00"), // время отправки (для еженедельного/ежемесячного отчета)
    val sendWeekDay: List<Int> = listOf(1), // дни недели для отправки отчета
    val sendMonthDay: List<Int> = listOf(1), // числа месяца для отправки отчета (32 - последний день месяца)
    val nameInHeader: Boolean = false, // Выводить название отчета в заголовке сообщения
    val workerIsActive: Boolean = true, // Галка активности отчета
    val sendDateTimeList: List<String> = listOf(), // список дат-времени для отправки отчетов тип 4
    val preliminarySendBeforeDays: Long = 0, // За сколько дней до события разово отправить (0 - не отправлять)
    val preliminarySendTime: String = "10:00", // Время предварительной разовой отправки
)
