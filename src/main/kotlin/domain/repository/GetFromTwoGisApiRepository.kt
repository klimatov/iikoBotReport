package domain.repository

import data.remoteAPI.twoGis.TwoGisReviewsDTO

interface GetFromTwoGisApiRepository {
    fun getData(firm: String): TwoGisReviewsDTO?
}