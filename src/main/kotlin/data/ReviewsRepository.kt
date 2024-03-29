package data

import data.database.reviewsWorkers.ReviewsWorkersDB
import data.database.reviewsWorkers.mapToReviewsWorkerParam
import data.database.reviewsWorkers.mapToReviewsWorkersDTO
import models.ReviewsWorkerParam

class ReviewsRepository {
    private val tag = this::class.java.simpleName
    fun get(): MutableMap<String, ReviewsWorkerParam> {
        return ReviewsWorkersDB.getAll().associate { it.workerId to it.mapToReviewsWorkerParam() }.toMutableMap()
    }

    fun set(workerList: MutableMap<String, ReviewsWorkerParam>?) {
        workerList?.forEach { (_, reviewsWorkerParam) ->
            ReviewsWorkersDB.insert(reviewsWorkerParam.mapToReviewsWorkersDTO())
        }
    }

    fun delete(workerId: String) {
        ReviewsWorkersDB.deleteByWorkerId(workerId)
    }

    // TODO: методы для апдейта отдельных воркеров и т.п.

    /*    fun getShownList(): MutableMap<Int, String> {
            val serializedData = FileOperations().read("shownreviews.cfg")
            val type = object : TypeToken<MutableMap<Int, String>>() {}.type
            var shownReviews =
                Gson().fromJson<MutableMap<Int, String>>(serializedData, type)
            if (shownReviews == null) shownReviews = mutableMapOf()
            return shownReviews
        }

        fun setShownList(shownReviews: MutableMap<Int, String>?) {
            if (shownReviews != null) {
                val serializedData = Gson().toJson(shownReviews)
                FileOperations().write("shownreviews.cfg", serializedData)
            }
        }*/
}