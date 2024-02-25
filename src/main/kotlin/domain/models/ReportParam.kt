package domain.models

import models.ReportWorkerParam

class ReportParam(
    val reportId: String, // ID отчета в iiko
    val reportPeriod: Int, // период данных для формирования отчета из iiko
    // (0 - сегодня, n - количество дней, -1 с начала недели, -2 с начала месяца, -3 с начала квартала, -4 с начала года)
    val sendChatId: List<Long> = listOf(), // список ID чатов/юзеров куда будет отправляться отчет
    val messageHeader: Boolean, // отображать ли заголовок в отчете?
    val messageSuffix: Map<Int, String>, // суфикс руб./шт. в колонке номер Int
    val messageAmount: Int, // доп. строка с суммой колонки номер Int (0 если не выводим)
    val messageWordLimit: Map<Int, Int> = mapOf(), // в колонке номер Int количество слов не более Int
    val nameInHeader: Boolean = false, // Выводить название отчета в заголовке сообщения
    val workerIsActive: Boolean = true, // Галка активности отчета
    val workerName: String = "", // название отчета
)

 fun ReportWorkerParam.mapToReportParam(): ReportParam = ReportParam(
     reportId = reportId,
     reportPeriod = reportPeriod,
     sendChatId = workerParam.sendChatId,
     messageHeader = messageHeader,
     messageSuffix = messageSuffix,
     messageAmount = messageAmount,
     messageWordLimit = messageWordLimit,
     nameInHeader = workerParam.nameInHeader,
     workerIsActive = workerParam.workerIsActive,
     workerName = workerParam.workerName
 )