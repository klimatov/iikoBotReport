package domain.models

class ReviewsParam(
    val sendChatId: List<Long> = listOf(), // список ID чатов/юзеров куда будет отправляться отчет
    val workerName: String = "", // название отзыва
    val nameInHeader: Boolean = false, // Выводить название отчета в заголовке сообщения
    val reviewsText: String = "", // текст сообщения о новом отзыве (с шаблонами)
    val workerId: String
)