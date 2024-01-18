package domain.usecases

import domain.models.BirthdayParam
import domain.models.EmployeeModel
import domain.models.Reviews
import domain.models.ReviewsParam
import domain.repository.BotRepository

class SendReviewsMessage(private val botRepository: BotRepository) {
    private val tag = this::class.java.simpleName

    suspend fun execute(reviewsParam: ReviewsParam, reviewsList: List<Reviews>): Boolean {
        var resultFlag = true
        reviewsList.forEach { review ->
            val messageText = FormatText().review(reviewsParam, review)
            if ((messageText.isNotEmpty()) && (reviewsParam.sendChatId.isNotEmpty())) {
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