package models

data class TwoGisWorkerParam(
    val twoGisText: String = "", // текст сообщения о новом отзыве (с шаблонами)
    val sendIfRating: List<Int> = listOf(1,2,3,4,5), // количество звезд для отправки отзыва
    val workerParam: WorkerParam = WorkerParam(),
)