package domain.models

data class ReminderParam(
    val sendChatId: Long, // ID чата/юзера куда будет отправляться отчет
    val nameInHeader: Boolean = false, // Выводить название отчета в заголовке сообщения
    val workerName: String = "", // название отчета
    val reminderText: String = "", // текст напоминания
)
