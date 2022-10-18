package models

data class ReportWorkerParam(
    val reportId: String = "", // ID отчета в iiko
    val reportPeriod: Int = 0, // период данных для формирования отчета из iiko
    // (0 - сегодня, n - количество дней, -1 с начала недели, -2 с начала месяца, -3 с начала квартала, -4 с начала года)
    val messageHeader: Boolean = true, // отображать ли заголовок в отчете?
    val messageSuffix: Map<Int, String> = mapOf(), // суфикс руб./шт. в колонке номер Int
    val messageAmount: Int = 0, // доп. строка с суммой колонки номер Int (0 если не выводим)
    val messageWordLimit: Map<Int, Int> = mapOf(), // в колонке номер Int количество слов не более Int
    val workerParam: WorkerParam = WorkerParam()
)