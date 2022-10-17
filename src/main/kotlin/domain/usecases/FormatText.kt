package domain.usecases

import domain.models.MessageParam
import domain.models.ReminderParam
import java.text.DecimalFormat
import kotlin.math.roundToInt

class FormatText {
    private val tag = this::class.java.simpleName

    fun reminder(reminderParam: ReminderParam): String {
        var resultMessage = ""

        if (reminderParam.nameInHeader) resultMessage += reminderParam.workerName + "\n"
        resultMessage += reminderParam.reminderText

        return resultMessage
    }

    fun report(messageParam: MessageParam): String {
        var tableMsg = messageParam.reportResult.table
        var resultMessage = ""
        var sum: Double = 0.0

        if (messageParam.nameInHeader) resultMessage += messageParam.workerName + "\n"
        tableMsg?.forEachIndexed() { indexRow, row ->
            if ((!messageParam.messageHeader) && (indexRow == 0)) return@forEachIndexed // убираем названия колонок если false в настройках

            resultMessage += row.mapIndexed { indexCell, cell ->  // перебираем строку по ячейкам

                var resultCell = cell

                if (messageParam.messageWordLimit.contains(indexCell)) // обрезаем лишние слова
                    resultCell = cell.split(" ").take(messageParam.messageWordLimit[indexCell] ?: 1).joinToString(" ")

                if (resultCell.toDoubleOrNull() != null) resultCell = insertSpace(resultCell)
                // убираем числа после точки и вставляем пробелы

                if (messageParam.messageSuffix.contains(indexCell)) resultCell += messageParam.messageSuffix[indexCell] else resultCell
                // проставляем суффиксы руб./шт.

                resultCell

            }.joinToString(separator = " - ") { it } // сворачиваем все значения в строку
            if (messageParam.oldReport.table?.contains(row) == false) {
                resultMessage += "\uD83D\uDD1D"
            } // если изменения добавляем значок
            resultMessage += "\n"

            if ((messageParam.messageAmount > 0) && (row.size >= messageParam.messageAmount)) { // доп. строка с суммой колонки номер N
                val cellValue = row[messageParam.messageAmount - 1].toDoubleOrNull()
                if (cellValue != null) sum += cellValue // если сконвертился то плюсуем
            }
        }
        if (messageParam.messageAmount > 0) { // добавляем строку с итогом если есть в настройках
            resultMessage += "Итого: ${insertSpace(sum.toString())}${messageParam.messageSuffix[messageParam.messageAmount - 1] ?: ""}"
        }

        return resultMessage
    }

    private fun insertSpace(number: String): String {
        var result = number
        if (number.toDoubleOrNull() != null) {
            result = DecimalFormat("###,###,###,###,###").format(number.toDouble()).replace(",", " ")
        }
        return result
    }
}