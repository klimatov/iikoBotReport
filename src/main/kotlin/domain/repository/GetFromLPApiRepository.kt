package domain.repository

import domain.models.ReviewsRequestParam

interface GetFromLPApiRepository {
    fun getBootData(): String?
    fun getReviewList(reviewsRequestParam: ReviewsRequestParam): String?
    fun getClientData(clientId: Int): String?
}