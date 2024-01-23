package models

data class ReviewsDataParam(
    var workerId: String,
    val shownReviews: MutableList<ShownReviews> = mutableListOf()
)

data class ShownReviews(
    val shownReviewId: Int,
    val shownReviewCreatedTimestamp: String? = null
)