package domain.models

import models.ReminderWorkerParam

data class ReminderParam(
    val sendChatId: List<Long> = listOf(), // список ID чатов/юзеров куда будет отправляться отчет
    val nameInHeader: Boolean = false, // Выводить название отчета в заголовке сообщения
    val workerName: String = "", // название отчета
    val reminderText: String = "", // текст напоминания
    val reminderPreliminaryText: String = "", // текст напоминания для предотправки
)
fun ReminderWorkerParam.mapToReminderParam(): ReminderParam = ReminderParam(
    sendChatId = workerParam.sendChatId,
    nameInHeader = workerParam.nameInHeader,
    workerName = workerParam.workerName,
    reminderText = reminderText,
    reminderPreliminaryText = reminderPreliminaryText,
)
