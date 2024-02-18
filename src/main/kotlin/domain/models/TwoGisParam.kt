package domain.models

class TwoGisParam(
    val sendChatId: List<Long> = listOf(), // список ID чатов/юзеров куда будет отправляться отчет
    val workerName: String = "", // название отзыва
    val nameInHeader: Boolean = false, // Выводить название отчета в заголовке сообщения
    val twoGisText: String = "", // текст сообщения о новом отзыве (с шаблонами)
    val workerId: String,
    val sendIfRating: List<Int> = listOf(1,2,3,4,5), // количество звезд для отправки отзыва
)