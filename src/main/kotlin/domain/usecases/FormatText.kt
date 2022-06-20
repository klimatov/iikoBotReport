package domain.usecases

import domain.models.MessageParam
import domain.models.ReportResult

class FormatText {

    fun report(messageParam: MessageParam): String {
        var tableMsg = messageParam.reportResult.table
        var resultMessage = ""


        tableMsg?.forEachIndexed() { index, strings ->
            if ((!messageParam.messageHeader) && (index == 0)) return@forEachIndexed // убираем заголовок если false в настройках

            resultMessage += strings.mapIndexed { index, row ->  // проставляем суффиксы руб./шт. и т.п.
                if (messageParam.messageSuffix.contains(index)) row + messageParam.messageSuffix[index] else row
            }.joinToString(separator = " - ") { it } // сворачиваем все значения в строку

            if (strings != messageParam.oldReport.table?.get(index)) resultMessage += "\uD83D\uDD1D" // если изменения добавляем значок
            resultMessage += "\n"

        }
        return resultMessage
    }
}