package domain.usecases

import domain.models.Client
import domain.models.Reviews
import domain.models.ReviewsParam
import domain.models.User
import domain.repository.BotRepository

class SendReviewsMessage(private val botRepository: BotRepository) {
    private val tag = this::class.java.simpleName

    suspend fun execute(
        reviewsParam: ReviewsParam,
        reviewsList: List<Reviews>,
        clientsList: MutableList<Client>,
//        outlets: List<Outlets>,
        userData: User
    ): Boolean {
        var resultFlag = true
        reviewsList.forEach { review ->
            val messageText = FormatText().review(
                reviewsParam = reviewsParam,
                review = review,
                client = clientsList.find { it.id == review.client } ?: Client(),
                userData = userData
            )
            if (
                (messageText.isNotEmpty())
                && (reviewsParam.sendChatId.isNotEmpty())
                && (reviewsParam.sendIfRating.contains(review.rating)) // проверяем настройку показа по звездам
                ) {
                reviewsParam.sendChatId.forEach {
                    if (!botRepository.sendMessageToChat(text = messageText, sendChatId = it)) {
                        resultFlag = false
                    }
                }
            } else resultFlag = false
        }
        return resultFlag // TODO: переделать на отправку результата по каждому адресату
    }
}