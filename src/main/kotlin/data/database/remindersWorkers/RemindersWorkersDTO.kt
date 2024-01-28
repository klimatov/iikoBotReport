package data.database.remindersWorkers

import kotlinx.serialization.Serializable
import models.ReminderWorkerParam
import models.WorkerParam

@Serializable
data class RemindersWorkersDTO(
    val reminderText: String,
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
    val sendDateTimeList: List<String>
)

fun ReminderWorkerParam.mapToRemindersWorkersDTO(): RemindersWorkersDTO = RemindersWorkersDTO(
    reminderText = reminderText,
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
    sendDateTimeList = workerParam.sendDateTimeList
)

fun RemindersWorkersDTO.mapToReminderWorkerParam(): ReminderWorkerParam = ReminderWorkerParam(
    reminderText = reminderText,
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
        sendDateTimeList = sendDateTimeList
    )
)