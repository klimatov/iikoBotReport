package domain.usecases

import domain.models.TwoGisReview
import domain.models.mapToTwoGisReview
import domain.repository.GetFromTwoGisApiRepository

class GetDataFromTwoGIS(private val getFromTwoGisApiRepository: GetFromTwoGisApiRepository) {
    private val tag = this::class.java.simpleName

    fun getReviewsList(firm: String): List<TwoGisReview> =
        getFromTwoGisApiRepository.getData(firm)?.reviews?.map {
            it.mapToTwoGisReview()
        }?.toList() ?: emptyList()
}