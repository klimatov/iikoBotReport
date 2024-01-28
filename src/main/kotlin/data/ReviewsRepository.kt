package data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import data.fileProcessing.FileOperations
import models.ReviewsWorkerParam

class ReviewsRepository {
    private val tag = this::class.java.simpleName
    fun get(): MutableMap<String, ReviewsWorkerParam> {
        val serializedData = FileOperations().read("reviews.cfg")
        val type = object : TypeToken<MutableMap<String, ReviewsWorkerParam>>() {}.type
        var workerList =
            Gson().fromJson<MutableMap<String, ReviewsWorkerParam>>(serializedData, type)
        if (workerList == null) workerList = mutableMapOf()
        return workerList
    }

    fun set(workerList: MutableMap<String, ReviewsWorkerParam>?) {
        if (workerList != null) {
            val serializedData = Gson().toJson(workerList)
            FileOperations().write("reviews.cfg", serializedData)
        }
    }

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