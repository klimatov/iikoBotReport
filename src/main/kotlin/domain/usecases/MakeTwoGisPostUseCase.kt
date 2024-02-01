package domain.usecases

import data.TwoGisDataRepository
import domain.models.TwoGisCompanyEnum
import domain.models.TwoGisParam
import domain.models.TwoGisReview
import domain.repository.BotRepository
import domain.repository.GetFromTwoGisApiRepository
import models.TwoGisDataParam
import models.TwoGisShownReviews
import utils.Logging
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MakeTwoGisPostUseCase(
    getFromTwoGisApiRepository: GetFromTwoGisApiRepository,
    private val botRepository: BotRepository
) {
    private val tag = this::class.java.simpleName
    private val getDataFromTwoGIS = GetDataFromTwoGIS(getFromTwoGisApiRepository)
    private var shownTwoGisReviewsList: MutableList<TwoGisShownReviews> = mutableListOf()

    suspend fun execute(twoGisParam: TwoGisParam) {
        // если список показанных отзывов пустой, то подгружаем из базы
        if (shownTwoGisReviewsList.isEmpty())
            TwoGisDataRepository.getByWorkerId(twoGisParam.workerId)?.shownReviews?.let {
                shownTwoGisReviewsList = it
            }

        // тут получаем список отзывов
        val twoGisReviewsList: MutableList<TwoGisReview> = mutableListOf()
        TwoGisCompanyEnum.values().forEach {
            twoGisReviewsList += getDataFromTwoGIS.getReviewsList(it.twoGisCompanyData.id)
        }

        // фильтр уже отправленных отзывов
        val sentReviewsList = twoGisReviewsList.filter { review ->
            shownTwoGisReviewsList.all { shownReview ->
                shownReview.shownReviewId != review.id
            }
        }

        // добавляем в список отправленных
        sentReviewsList.forEach { review ->
            review.id?.let {
                shownTwoGisReviewsList.add(
                    TwoGisShownReviews(it, review.dateCreated)
                )
                // ограничиваем список отправленных
//                if (shownReviewsList.size > 100) shownReviewsList.removeLast()

                //TODO: удаление старых отзывов по дате
            }
        }

        // если отправляли отзывы, то обновляем данные отправленных в БД
        if (sentReviewsList.isNotEmpty()) TwoGisDataRepository.setByWorkerId(
            TwoGisDataParam(
                workerId = twoGisParam.workerId,
                shownReviews = shownTwoGisReviewsList.toMutableList()
            )
        )

        Logging.d(tag, "New2GIS: $twoGisReviewsList")
        Logging.d(tag, "SentNow2GIS: $sentReviewsList")
        Logging.d(tag, "Shown2GIS: $shownTwoGisReviewsList")

        val sendResult =
            SendTwoGisMessage(botRepository = botRepository).execute(
                twoGisParam = twoGisParam,
                twoGisReviewsList = sentReviewsList
            )
        Logging.i(
            tag,
            "Отзывы ${twoGisParam.workerName} ${if (sendResult) "отправлено в чат" else "отправить в чат НЕ УДАЛОСЬ"}..."
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
