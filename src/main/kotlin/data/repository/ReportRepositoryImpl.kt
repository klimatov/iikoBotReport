package data.repository

import SecurityData.LOGIN_PASSWORD
import SecurityData.LOGIN_USER
import SecurityData.SERVER_IP
import SecurityData.SERVER_PORT
import domain.models.ReportParam
import domain.repository.ReportRepository
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

object ReportRepositoryImpl : ReportRepository {
    private val login = LOGIN_USER
    private val password = LOGIN_PASSWORD
    private val server = "$SERVER_IP:$SERVER_PORT"
    private var loginCookies: MutableMap<String, String> = mutableMapOf()
    private val path = "/resto/service/reports/report.jspx"

    init {
        refreshCookies()
    }

    override fun get(reportParam: ReportParam): Document? {
        try {
            if (checkCookies()) {
                val doc =
                    Jsoup.connect("$server$path")
                        .cookies(loginCookies)
                        .userAgent("Chrome/4.0.249.0 Safari/532.5")
                        .referrer(server)
                        .data(
                            "dateFrom",
                            reportParam.dateFrom,
                            "dateTo",
                            reportParam.dateTo,
                            "presetId",
                            reportParam.reportId
                        )
                        .get()
                if ((doc.connection().response().url().path == path) && (doc.connection().response()
                        .statusCode() == 200)
                ) {
                    return doc
                } else {
                    refreshCookies()
                    return null
                }
            } else return null
        } catch (e: Exception) {
            println(e)
            return null
        }
    }

    private fun checkCookies(): Boolean {
        if (loginCookies.isEmpty()) return refreshCookies() else return true
    }

    private fun refreshCookies(): Boolean {
        val res = Jsoup.connect("$server/resto/j_spring_security_check")
            .data("j_username", login, "j_password", password, "submit", "Log+in")
            .method(Connection.Method.POST)
            .execute()
        if (!((res.statusCode() == 200) && (res.url().path == "/resto/service/"))) {
            println("login failed")
            return false
        } else {
            loginCookies = res.cookies()
            println("login ok, cookies: $loginCookies")
            return true
        }
    }
}

