package data.remoteAPI.iiko

import SecurityData.LOGIN_PASSWORD
import SecurityData.LOGIN_USER
import SecurityData.SERVER_IP
import SecurityData.SERVER_PORT
import domain.repository.GetFromIikoApiRepository
import korlibs.crypto.sha1
import org.jsoup.Connection
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import utils.Logging

class GetFromIikoApiRepositoryImpl : GetFromIikoApiRepository {
    private val tag = this::class.java.simpleName
    private val login = LOGIN_USER
    private val password = LOGIN_PASSWORD.encodeToByteArray().sha1().hexLower
    private val server = "$SERVER_IP:$SERVER_PORT"
    private var loginKey: String = ""
    private val pathForGetEmployeesData = "/resto/api/employees"
    private val pathForLogin = "/resto/api/auth"
    private val pathForLogout = "/resto/api/logout"

//    init {
//        try {
//            login()
//        } catch (e: Exception) {
//            Logging.e(tag,e.toString())
//        }
//    }

    override fun getEmployees(): String? {
        try {
            if (login()) {
                val doc =
                    Jsoup.connect("${server}${pathForGetEmployeesData}")
                        .data("key", loginKey)
                        .userAgent("Chrome/4.0.249.0 Safari/532.5")
                        .referrer(server)
                        .get()
                if (doc.connection().response().statusCode() == 200) {
                    logout()
                    return doc.toString()
                } else {
                    Logging.e(tag, "Получить данные не удалось, возвращаем null")
                    return null
                }
            } else {
                Logging.e(tag, "Получить данные не удалось... Возвращаем null")
                return null
            }
        } catch (e: Exception) {
            Logging.e(tag, e.toString())
            return null
        }

    }

    private fun login(): Boolean {
        var responseStatusCode = 0
        var responseKey = ""

        try {
            val res = Jsoup.connect("$server$pathForLogin")
                .data("login", login, "pass", password)
                .method(Connection.Method.GET)
                .execute()
            responseStatusCode = res.statusCode()
            responseKey = res.cookies()["key"].toString()
        } catch (e: HttpStatusException) {
            responseStatusCode = e.statusCode
            Logging.e(tag, e.toString())
        }

        if (responseStatusCode == 200) {
            loginKey = responseKey
            Logging.i(tag, "login ok, key: $loginKey")
            return true
        } else {
            Logging.e(tag, "login failed, error code $responseStatusCode")
            return false
        }
    }

    private fun logout(): Boolean {
        var responseStatusCode = 0

        try {
            val res = Jsoup.connect("$server$pathForLogout")
                .data("key", loginKey)
                .method(Connection.Method.GET)
                .execute()
            responseStatusCode = res.statusCode()
        } catch (e: HttpStatusException) {
            responseStatusCode = e.statusCode
            Logging.e(tag, e.toString())
        }
        if ((responseStatusCode == 200) || (responseStatusCode == 401)) {
            Logging.i(tag, "logout ok")
            return true
        } else {
            Logging.e(tag, "logout failed, error code $responseStatusCode")
            return false
        }
    }


}