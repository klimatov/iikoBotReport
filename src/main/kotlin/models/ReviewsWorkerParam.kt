package models

data class ReviewsWorkerParam(
    val reviewText: String = "", // текст сообщения о новом отзыве (с шаблонами)
    val updateFrequency: Long = 0, // частота запроса к серверу отзывов (в минутах)
    val workerParam: WorkerParam = WorkerParam()
)