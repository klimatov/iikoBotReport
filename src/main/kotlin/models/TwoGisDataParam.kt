package models

data class TwoGisDataParam(
    var workerId: String,
    val shownReviews: MutableList<TwoGisShownReviews> = mutableListOf()
)

data class TwoGisShownReviews(
    val shownReviewId: String,
    val shownReviewCreatedTimestamp: String? = null
)