package data

import data.database.reviewsData.ReviewsDataDB
import data.database.reviewsData.mapToReviewsDataDTO
import data.database.reviewsData.mapToReviewsDataParam
import models.ReviewsDataParam

object ReviewsDataRepository {
    private val tag = this::class.java.simpleName
    fun get(): List<ReviewsDataParam> {
        return ReviewsDataDB.getAll().map { it.mapToReviewsDataParam() }.toList()
    }

    fun set(reviewsDataParamList: List<ReviewsDataParam>) {
        reviewsDataParamList.forEach { reviewsData ->
            ReviewsDataDB.insert(reviewsData.mapToReviewsDataDTO())
        }
    }

    fun setByWorkerId(reviewsDataParam: ReviewsDataParam) {
            ReviewsDataDB.insert(reviewsDataParam.mapToReviewsDataDTO())
    }

    fun delete(workerId: String) {
        ReviewsDataDB.deleteByWorkerId(workerId)
    }

    fun getByWorkerId(workerId: String): ReviewsDataParam? =
        ReviewsDataDB.getByWorkerId(workerId)?.mapToReviewsDataParam()


}