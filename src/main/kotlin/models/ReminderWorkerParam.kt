package models

data class ReminderWorkerParam(
    val reminderText: String = "", // текст напоминания
    val workerParam: WorkerParam = WorkerParam()
)
