package data.database.twoGisData

import kotlinx.serialization.Serializable
import models.TwoGisDataParam
import models.TwoGisShownReviews


@Serializable
data class TwoGisDataDTO(
    var workerId: String,
    val twoGisShownReviews: List<TwoGisShownReviewsDTO>
)

@Serializable
data class TwoGisShownReviewsDTO(
    val shownReviewId: String,
    val shownReviewCreatedTimestamp: String? = null,
    val shownReviewObjectId: String? = null,
)

fun TwoGisDataParam.mapToTwoGisDataDTO(): TwoGisDataDTO = TwoGisDataDTO(
    workerId = workerId,
    twoGisShownReviews = shownReviews.map {
        TwoGisShownReviewsDTO(
            shownReviewId = it.shownReviewId,
            shownReviewCreatedTimestamp = it.shownReviewCreatedTimestamp,
            shownReviewObjectId = it.shownReviewObjectId,
        )
    }.toList()
)

fun TwoGisDataDTO.mapToTwoGisDataParam(): TwoGisDataParam = TwoGisDataParam(
    workerId = workerId,
    shownReviews = twoGisShownReviews.map {
        TwoGisShownReviews(
            shownReviewId = it.shownReviewId,
            shownReviewCreatedTimestamp = it.shownReviewCreatedTimestamp,
            shownReviewObjectId = it.shownReviewObjectId
        )
    }.toMutableList()
)