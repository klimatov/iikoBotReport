import SecurityData.LOGIN_PASSWORD
import SecurityData.LOGIN_USER
import SecurityData.REPORT_ID
import SecurityData.SERVER_IP
import SecurityData.SERVER_PORT
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.parser.Parser

fun main() {
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
    } else {
        println("login ok")
        val doc = dotToDash(Jsoup.connect("$server/resto/service/reports/report.jspx")
            .cookies(loginCookies)
            .userAgent("Chrome/4.0.249.0 Safari/532.5")
            .referrer(server)
            .data("dateFrom", "17.06.2022", "dateTo", "17.06.2022", "presetId", REPORT_ID)
            .get())

        // парсим заголовки таблицы с id
        val columns = mutableMapOf<String, String>()
        for (element in doc.getElementsByTag("head")) {
            element.getElementsByTag("grouping").forEach { columns.put(it.id(), it.text()) }
            element.getElementsByTag("values").forEach { columns.put(it.id(), it.text()) }
        }

        // создаем таблицу (list of lists) и 0 строкой вносим заголовки
        val table: MutableList<MutableList<String>> = (mutableListOf(columns.values.toMutableList()))

        // перебираем все строки и вносим в таблицу
        for (element in doc.getElementsByTag("data")) { // перебираем строки исходной таблицы
            val row = mutableListOf<String>() // создаем пустую строку

            columns.forEach { name -> // перебираем все id колонок таблицы
                row.add(element.select(name.key).first()?.text().toString()) // добавляем ячейку по id
            }
            table.add(row) // добавляем полученную строку в таблицу
        }
        table.forEach { println(it) }
    }
}

fun dotToDash(doc: Document): Document {     // замена "." на "-" внутри тэгов
    var text = ""
    var flag = false
    for (char in doc.toString()) {
        when (char.toString()) {
            "<" -> {
                flag = true
                text += char
            }
            ">" -> {
                flag = false
                text += char
            }
            "." -> {
                if (flag) text += "-" else text += char
            }
            else -> text += char
        }
    }
    return Jsoup.parse(text, Parser.xmlParser())
}