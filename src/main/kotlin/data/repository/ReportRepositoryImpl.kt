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
import org.jsoup.parser.Parser

class ReportRepositoryImpl() : ReportRepository {
    override fun get(reportParam: ReportParam): Document? {

        val login = LOGIN_USER
        val password = LOGIN_PASSWORD
        val server = "$SERVER_IP:$SERVER_PORT"
        val res = Jsoup.connect("$server/resto/j_spring_security_check")
            .data("j_username", login, "j_password", password, "submit", "Log+in")
            .method(Connection.Method.POST)
            .execute()
        val loginCookies = res.cookies()
        if (!((res.statusCode() == 200) && (res.url().path == "/resto/service/"))) {
            println("login failed")
            return null
        } else {
            println("login ok")
            val doc =
                Jsoup.connect("$server/resto/service/reports/report.jspx")
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

            return doc
        }
    }


}

