package domain.usecases

import data.ReviewsDataRepository
import domain.models.*
import domain.repository.BotRepository
import domain.repository.GetFromLPApiRepository
import models.ReviewsDataParam
import models.ShownReviews
import utils.Logging
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MakeReviewsPostUseCase(
    getFromLPApiRepository: GetFromLPApiRepository,
    private val botRepository: BotRepository
) {
    private val tag = this::class.java.simpleName
    private val getDataFromLP = GetDataFromLP(getFromLPApiRepository)
    private var bootData = BootDataModel()
    private var clientsList: MutableList<Client> = mutableListOf()
    private var shownReviewsList: MutableList<ShownReviews> = mutableListOf()

    suspend fun execute(reviewsParam: ReviewsParam) {
        // если список показанных отзывов пустой, то подгружаем из базы
        if (shownReviewsList.isEmpty())
            ReviewsDataRepository.getByWorkerId(reviewsParam.workerId)?.shownReviews?.let {
                shownReviewsList = it
            }


        // пробуем получить учетные данные с сервера
        val tempBootData = getDataFromLP.getBootData()
        // если все ок, то обновляем учетные данные
        if (tempBootData != BootDataModel()) bootData = tempBootData

        // тут получаем список отзывов
        val reviewsRequestParam = mapToReviewsRequestParam(reviewsParam)
        val reviewsList = getDataFromLP.getReviewsList(reviewsRequestParam)

        // фильтр уже отправленных отзывов
        val sentReviewsList = reviewsList.filter { review ->
            shownReviewsList.all { shownReview ->
                shownReview.shownReviewId != review.id
            }
        }

        // получаем данные клиентов для отзывов
        sentReviewsList.forEach { review ->
            if (review.client != null) {
                val tempClient = getDataFromLP.getClientData(review.client!!)
                if (tempClient != null) clientsList.add(tempClient)
            }
        }

        // добавляем в список отправленных
        sentReviewsList.forEach { review ->
            review.id?.let {
                shownReviewsList.add(
                    ShownReviews(it, review.createdTimestamp.toString())
                )
                // ограничиваем список отправленных
//                if (shownReviewsList.size > 100) shownReviewsList.removeLast()

                //TODO: удаление старых отзывов по дате
            }
        }

        // если отправляли отзывы, то обновляем данные отправленных в БД
        if (sentReviewsList.isNotEmpty()) ReviewsDataRepository.setByWorkerId(
            ReviewsDataParam(
                workerId = reviewsParam.workerId,
                shownReviews = shownReviewsList.toMutableList()
            )
        )

//        Logging.d(tag, "New: $reviewsList")
//        Logging.d(tag, "SentNow: $sentReviewsList")
//        Logging.d(tag, "Shown: $shownReviewsList")

        val sendResult =
            SendReviewsMessage(botRepository = botRepository).execute(
                reviewsParam = reviewsParam,
                reviewsList = sentReviewsList,
                clientsList = clientsList,
                userData = bootData.user ?: User()
//                outlets = bootData.user?.partner?.outlets ?: emptyList()
            )
        Logging.i(
            tag,
            "Отзывы ${reviewsParam.workerName} ${if (sendResult) "отправлено в чат" else "отправить в чат НЕ УДАЛОСЬ"}..."
        )
    }

    private fun mapToReviewsRequestParam(reviewsParam: ReviewsParam): ReviewsRequestParam {
        //Logging.d(tag, bootData.toString())
        return ReviewsRequestParam(
            clientID = null,
            employees = null,
            length = 5, // ok
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
