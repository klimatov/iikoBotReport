package domain.usecases

import domain.models.MessageParam
import kotlin.math.roundToInt

class FormatText {
    private val tag = this::class.java.simpleName
    fun report(messageParam: MessageParam): String {
        var tableMsg = messageParam.reportResult.table
        var resultMessage = ""
        var sum: Double = 0.0

        if (messageParam.nameInHeader) resultMessage +=messageParam.workerName + "\n"
        tableMsg?.forEachIndexed() { indexRow, row ->
            if ((!messageParam.messageHeader) && (indexRow == 0)) return@forEachIndexed // убираем заголовок если false в настройках

            resultMessage += row.mapIndexed { indexCell, cell ->  // обрезаем слова, проставляем суффиксы руб./шт.

                var resultCell = cell
                if (messageParam.messageWordLimit.contains(indexCell))
                    resultCell = cell.split(" ").take(messageParam.messageWordLimit[indexCell] ?: 1).joinToString(" ")

                if (messageParam.messageSuffix.contains(indexCell)) resultCell += messageParam.messageSuffix[indexCell] else cell

                resultCell

            }.joinToString(separator = " - ") { it } // сворачиваем все значения в строку
            if (messageParam.oldReport.table?.contains(row) == false) {resultMessage += "\uD83D\uDD1D"} // если изменения добавляем значок
//            if (row != messageParam.oldReport.table?.get(indexRow)) resultMessage += "\uD83D\uDD1D"
            resultMessage += "\n"

            if ((messageParam.messageAmount > 0)&&(row.size >= messageParam.messageAmount)) { // доп. строка с суммой колонки номер N
                val cellValue = row[messageParam.messageAmount-1].toDoubleOrNull()?.roundToInt()
                if (cellValue!=null) sum +=cellValue // если сконвертился то плюсуем
            }
        }
        if (messageParam.messageAmount > 0) { // добавляем строку с итогом если есть в настройках
            resultMessage += "Итого: $sum${messageParam.messageSuffix[messageParam.messageAmount - 1] ?: ""}"
        }

        return resultMessage
    }
}