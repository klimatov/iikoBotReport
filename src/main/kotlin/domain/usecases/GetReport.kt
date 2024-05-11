package domain.usecases

import domain.models.ReportRequestParam
import domain.models.ReportResult
import domain.repository.ReportRepository
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.parser.Parser

class GetReport(private val reportRepository: ReportRepository) {
    private val tag = this::class.java.simpleName
    fun execute(reportRequestParam: ReportRequestParam): ReportResult {
        val doc = dotToDash(
            doc = reportRepository.get(reportRequestParam)
        )
        // парсим заголовки таблицы с id
        val columns = mutableMapOf<String, String>()
        if (doc != null) {
            for (element in doc.getElementsByTag("head")) {
                element.getElementsByTag("grouping").forEach { columns[it.id()] = it.text() }
                element.getElementsByTag("values").forEach { columns[it.id()] = it.text() }
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

            return ReportResult(table = table)
        } else return ReportResult(table = null)
    }

    private fun dotToDash(doc: Document?): Document {     // замена "." на "-" внутри тэгов
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
}