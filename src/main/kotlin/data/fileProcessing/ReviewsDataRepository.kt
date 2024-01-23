package data.fileProcessing

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import models.ReviewsWorkerParam
object ReviewsDataRepository {
    private val tag = this::class.java.simpleName
        fun get(): MutableMap<Int, String> {
            val serializedData = FileOperations().read("reviews_data.cfg")
            val type = object : TypeToken<MutableMap<Int, String>>() {}.type
            var shownReviews =
                Gson().fromJson<MutableMap<Int, String>>(serializedData, type)
            if (shownReviews == null) shownReviews = mutableMapOf()
            return shownReviews
        }

        fun set(shownReviews: MutableMap<Int, String>?) {
            if (shownReviews != null) {
                val serializedData = Gson().toJson(shownReviews)
                FileOperations().write("reviews_data.cfg", serializedData)
            }
        }
}