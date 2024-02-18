package domain.usecases

import domain.models.TwoGisParam
import domain.models.TwoGisReview
import domain.repository.BotRepository

class SendTwoGisMessage(private val botRepository: BotRepository) {
    private val tag = this::class.java.simpleName

    suspend fun execute(
        twoGisParam: TwoGisParam,
        twoGisReviewsList: List<TwoGisReview>,
    ): Boolean {
        var resultFlag = true
        twoGisReviewsList.forEach { review ->
            val messageText = FormatText().twoGisReview(
                twoGisParam = twoGisParam,
                twoGisReview = review
            )
            if (
                (messageText.isNotEmpty())
                && (twoGisParam.sendChatId.isNotEmpty())
                && (twoGisParam.sendIfRating.contains(review.rating)) // проверяем настройку показа по звездам
                ) {
                twoGisParam.sendChatId.forEach {
                    if (!botRepository.sendMessageToChat(text = messageText, sendChatId = it)) {
                        resultFlag = false
                    }
                }
            } else resultFlag = false
        }
        return resultFlag // TODO: переделать на отправку результата по каждому адресату
    }
}