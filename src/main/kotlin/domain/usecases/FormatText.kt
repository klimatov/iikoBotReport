package domain.usecases

import domain.models.ReportResult

class FormatText {

    fun report(reportResult: ReportResult):String {
        var resultMessage = ""
        reportResult.table?.forEach { row -> resultMessage += row.joinToString { it }+"\n" }
        return resultMessage
    }
}