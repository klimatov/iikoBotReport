package data.repository

import SecurityData.LOGIN_PASSWORD
import SecurityData.LOGIN_USER
import SecurityData.SERVER_IP
import SecurityData.SERVER_PORT
import domain.models.ReportRequestParam
import domain.repository.ReportRepository
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import utils.Logging
object ReportRepositoryImpl : ReportRepository {
    private val tag = this::class.java.simpleName
    private val login = LOGIN_USER
    private val password = LOGIN_PASSWORD
    private val server = "$SERVER_IP:$SERVER_PORT"
    private var loginCookies: MutableMap<String, String> = mutableMapOf()
    private val path = "/resto/service/reports/report.jspx"
    private val pathForCheckCookies = "/resto/service/reports/report.css"

    init {
        try {
            refreshCookies()
        } catch (e: Exception) {
            Logging.e(tag,e.toString())
        }
    }

    override fun get(reportRequestParam: ReportRequestParam): Document? {
        try {
            if (checkCookies()) {
                val doc =
                    Jsoup.connect("$server$path")
                        .cookies(loginCookies)
                        .userAgent("Chrome/4.0.249.0 Safari/532.5")
                        .referrer(server)
                        .data(
                            "dateFrom",
                            reportRequestParam.dateFrom,
                            "dateTo",
                            reportRequestParam.dateTo,
                            "presetId",
                            reportRequestParam.reportId
                        )
                        .get()
                if ((doc.connection().response().url().path == path) && (doc.connection().response()
                        .statusCode() == 200)
                ) {
                    return doc
                } else {
                    Logging.e(tag,"Получить данные не удалось, обновляем cookies, возвращаем null")
                    refreshCookies()
                    return null
                }
            } else {
                Logging.e(tag,"Получить данные не удалось... Возвращаем null")
                return null
            }
        } catch (e: Exception) {
            Logging.e(tag,e.toString())
            return null
        }
    }

    override fun getList(): Document? {
        try {
            if (checkCookies()) {
                val doc =
                    Jsoup.connect("$server$path")
                        .cookies(loginCookies)
                        .userAgent("Chrome/4.0.249.0 Safari/532.5")
                        .referrer(server)
                        .get()
                if ((doc.connection().response().url().path == path) && (doc.connection().response()
                        .statusCode() == 200)
                ) {
                    return doc
                } else {
                    Logging.e(tag,"Получить данные не удалось, обновляем cookies, возвращаем null")
                    refreshCookies()
                    return null
                }
            } else {
                Logging.e(tag,"Получить данные не удалось... Возвращаем null")
                return null
            }
        } catch (e: Exception) {
            Logging.e(tag,e.toString())
            return null
        }
    }

    private fun checkCookiesActual(): Boolean {
        val doc =
            Jsoup.connect("$server$pathForCheckCookies")
                .cookies(loginCookies)
                .userAgent("Chrome/4.0.249.0 Safari/532.5")
                .referrer(server)
                .get()
//        Logging.d(tag, doc.connection().response().url().path)
        if ((doc.connection().response().url().path == pathForCheckCookies) && (doc.connection().response()
                .statusCode() == 200)) return true else return false
    }

    private fun checkCookies(): Boolean {
        if (loginCookies.isEmpty()) return refreshCookies() else {
            val checkResult = if (!checkCookiesActual()) refreshCookies() else true
            return checkResult
        }
    }

    private fun refreshCookies(): Boolean {
        val res = Jsoup.connect("$server/resto/j_spring_security_check")
            .data("j_username", login, "j_password", password, "submit", "Log in", "_spring_security_remember_me", "on")
            .method(Connection.Method.POST)
            .execute()
        if (!((res.statusCode() == 200) && (res.url().path == "/resto/service/"))) {
            Logging.e(tag,"login failed")
            return false
        } else {
            loginCookies = res.cookies()
            Logging.i(tag,"login ok, cookies: $loginCookies")
            return true
        }
    }
}

