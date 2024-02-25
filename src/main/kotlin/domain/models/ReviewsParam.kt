package domain.models

import models.ReviewsWorkerParam

class ReviewsParam(
    val sendChatId: List<Long> = listOf(), // список ID чатов/юзеров куда будет отправляться отчет
    val workerName: String = "", // название отзыва
    val nameInHeader: Boolean = false, // Выводить название отчета в заголовке сообщения
    val reviewsText: String = "", // текст сообщения о новом отзыве (с шаблонами)
    val workerId: String,
    val sendIfRating: List<Int> = listOf(1,2,3,4,5), // количество звезд для отправки отзыва
)

fun ReviewsWorkerParam.mapToReviewsParam(): ReviewsParam {
    return ReviewsParam(
        sendChatId = workerParam.sendChatId,
        nameInHeader = workerParam.nameInHeader,
        workerName = workerParam.workerName,
        reviewsText = reviewsText,
        workerId = workerParam.workerId,
        sendIfRating = sendIfRating
    )
}