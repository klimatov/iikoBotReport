package data.repository

import SecurityData.LP_LOGIN_PASSWORD
import SecurityData.LP_LOGIN_USER
import SecurityData.LP_SERVER
import domain.models.ReviewsRequestParam
import domain.repository.GetFromLPApiRepository
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jsoup.Connection
import org.jsoup.Jsoup
import utils.Logging

object GetFromLPApiRepositoryImpl: GetFromLPApiRepository {
    private val tag = this::class.java.simpleName
    private val login = LP_LOGIN_USER
    private val password = LP_LOGIN_PASSWORD
    private val server = LP_SERVER
    private var loginCookies: MutableMap<String, String> = mutableMapOf()
    private val reviewPath = "/IPLPartner/api/reviews"
    private val bootDataPath = "/IPLPartner/api/boot_data"

    init {
        try {
            refreshCookies()
        } catch (e: Exception) {
            Logging.e(tag, e.toString())
        }
    }

    override fun getBootData(): String? {
        try {
            if (checkCookies()) {
//                val json = Json { encodeDefaults = true } // обрабатываем и нулабельные поля
//                val jsonBody = json.encodeToString(reviewsDTO)

                val doc =
                    Jsoup.connect("$server$bootDataPath")
                        .cookies(loginCookies)
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
                if (doc.statusCode() == 200) return doc.body().toString()
                else {
                    Logging.e(tag, "Получить данные не удалось, возвращаем null")
                    return null
                }
            } else {
                Logging.e(tag, "Получить данные не удалось, возвращаем null")
                return null
            }
        } catch (e: Exception) {
            Logging.e(tag, e.toString())
            return null
        }
    }

    override fun getReviewList(reviewsRequestParam: ReviewsRequestParam): String? {
        try {
            if (checkCookies()) {
                val json = Json { encodeDefaults = true } // обрабатываем и нулабельные поля
                Logging.d(tag, reviewsRequestParam.toString())
                val jsonBody = json.encodeToString(reviewsRequestParam)

                Logging.d(tag, jsonBody)

                val doc =
                    Jsoup.connect("$server$reviewPath")
                        .cookies(loginCookies)
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/json")
                        .followRedirects(true)
                        .ignoreHttpErrors(true)
                        .ignoreContentType(true)
                        .userAgent(
                            "Mozilla/5.0 AppleWebKit/537.36 (KHTML," +
                                    " like Gecko) Chrome/45.0.2454.4 Safari/537.36"
                        )
                        .method(Connection.Method.POST)
                        .requestBody(jsonBody)
                        .maxBodySize(1_000_000 * 30) // 30 mb ~
                        .timeout(0) // infinite timeout
                        .execute()
                Logging.d(tag, doc.statusCode().toString())
                Logging.d(tag, doc.statusMessage())
                if (doc.statusCode() == 200) return doc.body().toString()
                else {
                    Logging.e(tag, "Получить данные не удалось, возвращаем null")
                    return null
                }
            } else {
                Logging.e(tag, "Получить данные не удалось, возвращаем null")
                return null
            }
        } catch (e: Exception) {
            Logging.e(tag, e.stackTraceToString())
            return null
        }
    }

    private fun checkCookies(): Boolean {
        if (loginCookies.isEmpty()) return refreshCookies() else return true
    }

    private fun refreshCookies(): Boolean {
        val res = Jsoup.connect("$server/IPLPartner/j_spring_security_check")
            .data("j_username", login, "j_password", password, "submit", "", "_spring_security_remember_me", "on")
            .method(Connection.Method.POST)
            .execute()
        if (!((res.statusCode() == 200) && (res.url().path == "/IPLPartner/"))) {
            Logging.e(tag, "login to $LP_SERVER failed")
            return false
        } else {
            loginCookies = res.cookies()
//            loginCookies.remove("verification")
//            loginCookies.put("_ga", "GA1.2.1052942220.1663480560")
//            loginCookies.put("_gat", "1")
            Logging.i(tag, "login to $LP_SERVER ok, cookies: $loginCookies")
            return true
        }
    }
}
