package domain.models

import models.BirthdayWorkerParam

class BirthdayParam(
    val sendChatId: List<Long> = listOf(), // список ID чатов/юзеров куда будет отправляться отчет
    val workerName: String = "", // название поздравления
    val nameInHeader: Boolean = false, // Выводить название отчета в заголовке сообщения
    val birthdayText: String = "", // текст сообщения о дне рождения
    val sendBeforeDays: Long, // за сколько дней до ДР начать оповещать
    val preliminarySendBeforeDays: Long = 0, // За сколько дней до события разово отправить (0 - не отправлять)
    val birthdayPreliminaryText: String,// = "", // текст сообщения для предотправки
)

data class BirthdayValues(
    val bdYear: String = "",
    val bdMonth: String = "",
    val bdMonthWord: String = "",
    val bdDay: String = "",
    val bdDate: String = "",
    var newAge: Int = 0,
    val ageYearWord: String = "",
    val bdCounter: Int = 0,
)

fun BirthdayWorkerParam.mapToBirthdayParam(): BirthdayParam = BirthdayParam(
    sendChatId = workerParam.sendChatId,
    nameInHeader = workerParam.nameInHeader,
    workerName = workerParam.workerName,
    birthdayText = birthdayText,
    sendBeforeDays = sendBeforeDays,
    preliminarySendBeforeDays = workerParam.preliminarySendBeforeDays,
    birthdayPreliminaryText = birthdayPreliminaryText,
)