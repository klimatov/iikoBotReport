package domain.usecases

import domain.models.ReportResult

class FormatText {

    fun report(reportResult: ReportResult, oldResult: ReportResult): String {
        var resultMessage = ""
        reportResult.table?.forEachIndexed { index, strings ->
            resultMessage += strings.joinToString(separator = " - ") { it }
            if (strings != oldResult.table?.get(index)) resultMessage +="\uD83D\uDD1D"
            resultMessage += "\n"

        }
        println(resultMessage)
        return resultMessage
    }
}