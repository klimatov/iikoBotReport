package data.fileProcessing

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
}