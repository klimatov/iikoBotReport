package domain.usecases

import domain.models.*
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class FormatText {
    private val tag = this::class.java.simpleName

    fun reminder(reminderParam: ReminderParam): String {
        var resultMessage = ""

        if (reminderParam.nameInHeader) resultMessage += reminderParam.workerName + "\n"
        resultMessage += reminderParam.reminderText

        return resultMessage
    }

    fun birthday(birthdayParam: BirthdayParam, employee: EmployeeModel): String {
        var resultMessage = decodingTemplates(
            birthdayParam.birthdayText,
            employee
        )
        if (birthdayParam.nameInHeader) resultMessage = birthdayParam.workerName + "\n" + resultMessage

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

    private fun makeBirthdayValues(
        rawSourceDT: String,
        todayDate: LocalDate = LocalDate.now()
    ): BirthdayValues {
        val sourceDate = ZonedDateTime.parse(rawSourceDT).toLocalDate()
        val birthdayDate = LocalDate.of(todayDate.year, sourceDate.month, sourceDate.dayOfMonth)

        return BirthdayValues(
            bdYear = sourceDate.year.toString(),
            bdMonth = birthdayDate.monthValue.toString().padStart(2, '0'),
            bdMonthWord = when (birthdayDate.monthValue) {
                1 -> "января"
                2 -> "февраля"
                3 -> "марта"
                4 -> "апреля"
                5 -> "мая"
                6 -> "июня"
                7 -> "июля"
                8 -> "августа"
                9 -> "сентября"
                10 -> "октября"
                11 -> "ноября"
                12 -> "декабря"
                else -> ""
            },
            bdDay = birthdayDate.dayOfMonth.toString(),
            bdDate = birthdayDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
            newAge = (todayDate.year - sourceDate.year).toInt(),
            ageYearWord = ageYearWord(todayDate.year - sourceDate.year)
        )
    }

    private fun ageYearWord(age: Int): String {
        return age.let {
            if (it % 100 in 11..14) {
                "лет"
            } else {
                when ((it % 10).toInt()) {
                    1 -> "год"
                    2, 3, 4 -> "года"
                    else -> "лет"//0, 5, 6, 7, 8, 9
                }
            }
        }
    }

    private fun decodingTemplates(rawMessage: String, employee: EmployeeModel): String {
        val employeeBirthdayValues = makeBirthdayValues(employee.birthday)
        val regex = "\\[/?.*?\\]".toRegex()
        return rawMessage.replace(regex) {
            when (it.value.uppercase().substring(1, it.value.length - 1)) {
                "FIRSTNAME" -> employee.firstName
                "LASTNAME" -> employee.lastName
                "BDDAY" -> employeeBirthdayValues.bdDay
                "BDMONTHWORD" -> employeeBirthdayValues.bdMonthWord
                "AGE" -> employeeBirthdayValues.newAge.toString()
                "AGEYEARWORD" -> employeeBirthdayValues.ageYearWord
                else -> it.value
            }
        }

    }
}