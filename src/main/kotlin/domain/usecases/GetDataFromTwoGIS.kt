package domain.usecases

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import domain.models.ReviewsGIS
import domain.models.TwoGisReviewsModel
import domain.repository.GetFromTwoGisApiRepository
import utils.Logging

class GetDataFromTwoGIS(private val getFromTwoGisApiRepository: GetFromTwoGisApiRepository) {
    private val tag = this::class.java.simpleName

    fun getReviewsList(firm: String): List<ReviewsGIS> {
        try {
            val serializedData = getFromTwoGisApiRepository.getData(firm = firm)
            val type = object : TypeToken<TwoGisReviewsModel>() {}.type
            val deSerializedData = GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create()
                .fromJson<TwoGisReviewsModel>(serializedData, type)
            return deSerializedData.reviews ?: emptyList()
        } catch (e: Exception) {
            Logging.e(tag, e.stackTraceToString())
            return emptyList()
        }
    }

}