package models

data class ReviewsWorkerParam(
    val reviewsText: String = "", // текст сообщения о новом отзыве (с шаблонами)
    val workerParam: WorkerParam = WorkerParam()
)