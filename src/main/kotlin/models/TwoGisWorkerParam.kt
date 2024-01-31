package models

data class TwoGisWorkerParam(
    val twoGisText: String = "", // текст сообщения о новом отзыве (с шаблонами)
    val workerParam: WorkerParam = WorkerParam()
)