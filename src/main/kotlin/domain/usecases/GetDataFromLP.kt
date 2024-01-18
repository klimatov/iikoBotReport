package domain.usecases

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import domain.models.BootDataModel
import domain.models.Reviews
import domain.models.ReviewsModel
import domain.models.ReviewsRequestParam
import domain.repository.GetFromLPApiRepository
import utils.Logging

class GetDataFromLP(private val getFromLPApiRepository: GetFromLPApiRepository) {
    private val tag = this::class.java.simpleName

    fun getBootData(): BootDataModel {
        try {
            val serializedData = getFromLPApiRepository.getBootData()
            val type = object : TypeToken<BootDataModel>() {}.type
            val deSerializedData = GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create()
                .fromJson<BootDataModel>(serializedData, type)
            return deSerializedData ?: BootDataModel()
        } catch (e: Exception) {
            Logging.e(tag, e.toString())
            return BootDataModel()
        }
    }


    fun getReviewsList(reviewsRequestParam: ReviewsRequestParam): List<Reviews> {
        try {
            val serializedData = getFromLPApiRepository.getReviewList(reviewsRequestParam = reviewsRequestParam)
            val type = object : TypeToken<ReviewsModel>() {}.type
            val deSerializedData = GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create()
                .fromJson<ReviewsModel>(serializedData, type)
            return deSerializedData.data?.reviews ?: emptyList()
        } catch (e: Exception) {
            Logging.e(tag, e.toString())
            return emptyList()
        }
    }
}