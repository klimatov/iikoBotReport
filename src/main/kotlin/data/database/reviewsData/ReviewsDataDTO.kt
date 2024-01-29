package data.database.reviewsData

import kotlinx.serialization.Serializable
import models.ReviewsDataParam
import models.ShownReviews

@Serializable
data class ReviewsDataDTO(
    var workerId: String,
    val shownReviews: List<ShownReviewsDTO>
)

@Serializable
data class ShownReviewsDTO(
    val shownReviewId: Int,
    val shownReviewCreatedTimestamp: String? = null
)

fun ReviewsDataParam.mapToReviewsDataDTO(): ReviewsDataDTO = ReviewsDataDTO(
    workerId = workerId,
    shownReviews = shownReviews.map {
        ShownReviewsDTO(
            shownReviewId = it.shownReviewId,
            shownReviewCreatedTimestamp = it.shownReviewCreatedTimestamp
        )
    }.toList()
)

fun ReviewsDataDTO.mapToReviewsDataParam(): ReviewsDataParam = ReviewsDataParam(
    workerId = workerId,
    shownReviews = shownReviews.map {
        ShownReviews(
            shownReviewId = it.shownReviewId,
            shownReviewCreatedTimestamp = it.shownReviewCreatedTimestamp
        )
    }.toMutableList()
)