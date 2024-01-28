package data.database.reportsWorkers

import kotlinx.serialization.Serializable
import models.ReportWorkerParam
import models.WorkerParam

@Serializable
data class ReportsWorkersDTO(
    val reportId: String,
    val reportPeriod: Int,
    val messageHeader: Boolean,
    val messageSuffix: Map<Int, String>,
    val messageAmount: Int,
    val messageWordLimit: Map<Int, Int>,
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

fun ReportWorkerParam.mapToReportsWorkersDTO(): ReportsWorkersDTO = ReportsWorkersDTO(
    reportId = reportId,
    reportPeriod = reportPeriod,
    messageHeader = messageHeader,
    messageSuffix = messageSuffix,
    messageAmount = messageAmount,
    messageWordLimit = messageWordLimit,
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

fun ReportsWorkersDTO.mapToReportWorkerParam(): ReportWorkerParam = ReportWorkerParam(
    reportId = reportId,
    reportPeriod = reportPeriod,
    messageHeader = messageHeader,
    messageSuffix = messageSuffix,
    messageAmount = messageAmount,
    messageWordLimit = messageWordLimit,
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