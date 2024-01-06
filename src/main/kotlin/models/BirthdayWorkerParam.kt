package models

data class BirthdayWorkerParam(
    val birthdayText: String = "", // текст сообщения о дне рождения
    val sendBeforeDays: Long = 0, // за сколько дней до ДР начать оповещать
    val workerParam: WorkerParam = WorkerParam()
)