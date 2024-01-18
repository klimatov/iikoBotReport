package domain.usecases

import domain.models.BootDataModel
import domain.models.EmployeeModel
import domain.models.ReviewsParam
import domain.models.ReviewsRequestParam
import domain.repository.BotRepository
import domain.repository.GetFromLPApiRepository
import utils.Logging
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MakeReviewsPostUseCase(
    private val getFromLPApiRepository: GetFromLPApiRepository,
    private val botRepository: BotRepository
) {
    private val tag = this::class.java.simpleName
    private val getDataFromLP = GetDataFromLP(getFromLPApiRepository)
    private var bootData = BootDataModel()

    suspend fun execute(reviewsParam: ReviewsParam) {
        // пробуем получить учетные данные с сервера
        val tempBootData = getDataFromLP.getBootData()
        // если все ок, то обновляем учетные данные
        if (tempBootData != BootDataModel()) bootData = tempBootData

        // тут получаем список отзывов
        val reviewsRequestParam = mapToReviewsRequestParam(reviewsParam)
        val reviewsList = getDataFromLP.getReviewsList(reviewsRequestParam)

        //TODO: фильтр уже отправленных отзывов

        val sendResult =
            SendReviewsMessage(botRepository = botRepository).execute(reviewsParam, reviewsList)
        Logging.i(
            tag,
            "Отзывы ${reviewsParam.workerName} ${if (sendResult) "отправлено в чат" else "отправить в чат НЕ УДАЛОСЬ"}..."
        )
    }

    private fun mapToReviewsRequestParam(reviewsParam: ReviewsParam): ReviewsRequestParam {
        Logging.d(tag,bootData.toString())
        return ReviewsRequestParam(
            clientID = null,
            employees = null,
            length = 10, // ok
            offset = 0, // ok
            orderTypes = null,
            outlets = null,
            partnerID = bootData.user?.partnerId.toString(), //ok
            periodFrom = periodFrom(14),
            periodTo = null, // ok
            processed = null,
            ratings = null,
            sort = 1,
            withComment = true // ok
        )
    }

    private fun periodFrom(minusDays: Long): String =
        LocalDate
            .now()
            .minusDays(minusDays)
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            .toString()
            .plus("T17:00:00.000Z")


}
