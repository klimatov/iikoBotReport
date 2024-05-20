package models

data class ReminderWorkerParam(
    val reminderText: String = "", // текст напоминания
    val reminderPreliminaryText: String = "", // текст напоминания для предотправки
    val workerParam: WorkerParam = WorkerParam()
)
