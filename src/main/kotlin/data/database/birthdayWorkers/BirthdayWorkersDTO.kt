package data.database.birthdayWorkers

import kotlinx.serialization.Serializable
import models.BirthdayWorkerParam
import models.WorkerParam

@Serializable
data class BirthdayWorkersDTO(
    val birthdayText: String,
    val sendBeforeDays: Long,
    val workerId: String,
    val workerName: String,
    val sendChatId: List<Long>,
    val sendWhenType: Int,
    val sendPeriod: Int,
    val sendTime: List<String>,
    val sendWeekDay: List<Int>,
    val sendMonthDay: List<Int>,
    val nameInHeader: Boolean,
    val workerIsActive: Boolean,
    val sendDateTimeList: List<String>,
    val preliminarySendBeforeDays: Long,
    val preliminarySendTime: String,
)

fun BirthdayWorkerParam.mapToBirthdayWorkersDTO(): BirthdayWorkersDTO = BirthdayWorkersDTO(
    birthdayText = birthdayText,
    sendBeforeDays = sendBeforeDays,
    workerId = workerParam.workerId,
    workerName = workerParam.workerName,
    sendChatId = workerParam.sendChatId,
    sendWhenType = workerParam.sendWhenType,
    sendPeriod = workerParam.sendPeriod,
    sendTime = workerParam.sendTime,
    sendWeekDay = workerParam.sendWeekDay,
    sendMonthDay = workerParam.sendMonthDay,
    nameInHeader = workerParam.nameInHeader,
    workerIsActive = workerParam.workerIsActive,
    sendDateTimeList = workerParam.sendDateTimeList,
    preliminarySendBeforeDays = workerParam.preliminarySendBeforeDays,
    preliminarySendTime = workerParam.preliminarySendTime,
)

fun BirthdayWorkersDTO.mapToBirthdayWorkerParam(): BirthdayWorkerParam = BirthdayWorkerParam(
    birthdayText = birthdayText,
    sendBeforeDays = sendBeforeDays,
    workerParam = WorkerParam(
        workerId = workerId,
        workerName = workerName,
        sendChatId = sendChatId,
        sendWhenType = sendWhenType,
        sendPeriod = sendPeriod,
        sendTime = sendTime,
        sendWeekDay = sendWeekDay,
        sendMonthDay = sendMonthDay,
        nameInHeader = nameInHeader,
        workerIsActive = workerIsActive,
        sendDateTimeList = sendDateTimeList,
        preliminarySendBeforeDays = preliminarySendBeforeDays,
        preliminarySendTime = preliminarySendTime,
    )
)