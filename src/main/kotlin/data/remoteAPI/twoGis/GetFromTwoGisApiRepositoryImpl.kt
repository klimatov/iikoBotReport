package data.remoteAPI.twoGis

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import domain.repository.GetFromTwoGisApiRepository
import org.jsoup.Connection
import org.jsoup.Jsoup
import utils.Logging


object GetFromTwoGisApiRepositoryImpl : GetFromTwoGisApiRepository {
    private val tag = this::class.java.simpleName

    private val twoGisRequestMessagesLimit = 12
    private val twoGisRequestKey = "37c04fe6-a560-4549-b459-02309cf643ad"
    private val twoGisLinkHead = "https://public-api.reviews.2gis.com/2.0/branches/"
    private val twoGisLinkTail = "/reviews?key=$twoGisRequestKey&limit=$twoGisRequestMessagesLimit&sort_by=date_created"

    val branch = "70000001023172948"


    override fun getData(firm: String): TwoGisReviewsDTO? {
        try {
            val doc =
                Jsoup.connect("$twoGisLinkHead$firm$twoGisLinkTail")
//                        .cookies(loginCookies)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .followRedirects(true)
                    .ignoreHttpErrors(true)
                    .ignoreContentType(true)
                    .userAgent(
                        "Mozilla/5.0 AppleWebKit/537.36 (KHTML," +
                                " like Gecko) Chrome/45.0.2454.4 Safari/537.36"
                    )
                    .method(Connection.Method.GET)
                    //.requestBody(jsonBody)
                    .maxBodySize(1_000_000 * 30) // 30 mb ~
                    .timeout(0) // infinite timeout
                    .execute()
            if (doc.statusCode() == 200) return deSerialization(doc.body().toString())
            else {
                Logging.e(tag, "Получить данные не удалось, возвращаем null")
                return null
            }

        } catch (e: Exception) {
            Logging.e(tag, e.stackTraceToString())
            return null
        }
    }

    private fun deSerialization(serializedData: String): TwoGisReviewsDTO {
        val type = object : TypeToken<TwoGisReviewsDTO>() {}.type
        val deSerializedData = GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create()
            .fromJson<TwoGisReviewsDTO>(serializedData, type)
        return deSerializedData
    }

}

