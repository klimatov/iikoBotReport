package domain.repository

interface GetFromTwoGisApiRepository {
    fun getData(firm: String): String?
}