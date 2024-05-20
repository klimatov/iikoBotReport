package domain.usecases

import domain.models.*
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class FormatText {
    private val tag = this::class.java.simpleName

    fun reminder(reminderParam: ReminderParam): String {
        var resultMessage = ""

        if (reminderParam.nameInHeader) resultMessage += reminderParam.workerName + "\n"
        resultMessage += decodingReminderTemplates(reminderParam.reminderText)

        return resultMessage
    }

    fun birthday(birthdayParam: BirthdayParam, employee: EmployeeModel): String {
        var resultMessage = decodingBirthdayTemplates(
            birthdayParam.birthdayText,
            employee
        )
        if (birthdayParam.nameInHeader) resultMessage = birthdayParam.workerName + "\n" + resultMessage

        return resultMessage
    }

    fun review(reviewsParam: ReviewsParam, review: Reviews, client: Client, userData: User): String {
        var resultMessage = decodingReviewsTemplates(
            reviewsParam.reviewsText,
            review,
            client,
            userData.partner?.outlets?.find { it.id == review.outlet },
            userData.timeZone ?: "Asia/Krasnoyarsk"
        )
        if (reviewsParam.nameInHeader) resultMessage = reviewsParam.workerName + "\n" + resultMessage

        return resultMessage
    }

    fun twoGisReview(twoGisParam: TwoGisParam, twoGisReview: TwoGisReview): String {
        var resultMessage = decodingTwoGisTemplates(
            twoGisParam.twoGisText,
            twoGisReview
        )
        if (twoGisParam.nameInHeader) resultMessage = twoGisParam.workerName + "\n" + resultMessage

        return resultMessage
    }

    fun report(messageParam: MessageParam): String {
        val tableMsg = messageParam.reportResult.table
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
            ageYearWord = ageYearWord(todayDate.year - sourceDate.year),
            bdCounter = (birthdayDate.toEpochDay() - todayDate.toEpochDay()).toInt()
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

    private fun decodingReminderTemplates(
        rawMessage: String
    ): String {
        val regex = "\\[/?.*?\\]".toRegex()
        val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        val digitPool : List<Char> = ('0'..'9').toList()
        return rawMessage.replace(regex) {
            when (it.value.uppercase().substring(1, it.value.length - 1)) {
                "RANDOM" -> List(6) { charPool.random() }.joinToString("")
                "RANDOM4" -> List(4) { digitPool.random() }.joinToString("")
                "DATEOFEVENT" -> it.value
                else -> it.value
            }
        }

    }

    private fun decodingTwoGisTemplates(
        rawMessage: String,
        twoGisReview: TwoGisReview
    ): String {
        val regex = "\\[/?.*?\\]".toRegex()
        return rawMessage.replace(regex) {
            when (it.value.uppercase().substring(1, it.value.length - 1)) {
                "STARS" -> toStarsString(twoGisReview.rating)
                "OUTLET" -> TwoGisCompanyEnum.entries
                    .first { it.twoGisCompanyData.id == twoGisReview.objectId }.twoGisCompanyData.name.toString()

                "OBJECTID" -> twoGisReview.objectId.toString()
                "ID" -> twoGisReview.id.toString()
//                "REGIONID" -> twoGisReview.regionId.toString()
                "TEXT" -> twoGisReview.text.toString()
                "RATING" -> twoGisReview.rating.toString()
                "PROVIDER" -> twoGisReview.provider.toString()
//                "SOURCE" -> twoGisReview.source.toString()
                "ISHIDDEN" -> twoGisReview.isHidden.toString()
//                "HIDINGTYPE" -> twoGisReview.hidingType.toString()
//                "HIDINGREASON" -> twoGisReview.hidingReason.toString()
//                "URL" -> twoGisReview.url.toString()
                "LIKESCOUNT" -> twoGisReview.likesCount.toString()
                "COMMENTSCOUNT" -> twoGisReview.commentsCount.toString()
                "DATECREATED" -> convertDateTime(twoGisReview.dateCreated.toString())
//                "DATEEDITED" -> convertDateTime(twoGisReview.dateEdited.toString())
//                "ONMODERATION" -> twoGisReview.onModeration.toString()
//                "ISRATED" -> twoGisReview.isRated.toString()
//                "ISVERIFIED" -> twoGisReview.isVerified.toString()
                "USERID" -> twoGisReview.userGISid.toString()
                "USERREVIEWSCOUNT" -> twoGisReview.userGISreviewsCount.toString()
                "FIRSTNAME" -> twoGisReview.userGISfirstName.toString()
                "LASTNAME" -> twoGisReview.userGISlastName.toString()
                "NAME" -> twoGisReview.userGISname.toString()
                "USERPROVIDER" -> twoGisReview.userGISprovider.toString()
                "USERURL" -> twoGisReview.userGISurl.toString()
                "USERPUBLICID" -> twoGisReview.userGISpublicId.toString()
                else -> it.value
            }
        }
    }

    private fun decodingReviewsTemplates(
        rawMessage: String,
        review: Reviews,
        client: Client,
        outlets: Outlets?,
        timeZone: String
    ): String {
        val regex = "\\[/?.*?\\]".toRegex()
        return rawMessage.replace(regex) {
            when (it.value.uppercase().substring(1, it.value.length - 1)) {
                "STARS" -> toStarsString(review.rating)
                "ID" -> review.id.toString()
                //"TYPE" -> review.type.toString()
                //"STATE" -> review.state.toString()
                "TEXT" -> review.text.toString()
                "RATING" -> review.rating.toString()
                //"CLIENT" -> review.client.toString()
                "DATECREATED" -> convertDateTimeWithZone(review.createdTimestamp ?: "", timeZone)
                //"PROCESSED" -> review.processed.toString()
                "OUTLET" -> outlets?.name?.name.toString()//review.outlet.toString()
                "ORDER" -> review.order.toString()
                "TR_ID" -> review.transaction?.id.toString()
                "TR_TYPE" -> review.transaction?.type.toString()
                "TR_STATE" -> review.transaction?.state.toString()
                "TR_SUM" -> review.transaction?.sum.toString()
                "TR_CLIENT" -> review.transaction?.client.toString()
                "TR_PURCHASEAMOUNT" -> review.transaction?.purchaseAmount.toString()
                "TR_VALIDATEDTIMESTAMP" -> review.transaction?.validatedTimestamp.toString()
                "TR_OUTLET" -> review.transaction?.outlet.toString()
                "TR_VALIDATOR" -> review.transaction?.validator.toString()
                "TR_COUPON" -> review.transaction?.coupon.toString()
                "TR_VALIDATIONID" -> review.transaction?.validationID.toString()
                "CL_ID" -> client.id.toString()
                "AGE" -> client.age.toString()
                "BALANCE" -> client.balance.toString()
                "EMAIL" -> client.email.toString()
                "PHONE" -> client.phone.toString()
                "DATEOFBIRTH" -> convertDate(client.dateOfBirth ?: "")
                "LASTVISITEDTIME" -> convertDateTimeWithZone(client.lastVisitedTime ?: "", timeZone)
                "FIRSTNAME" -> client.firstName ?: ""
                "FULLNAME" -> client.fullName ?: ""
                "VISITS" -> client.visits.toString()
                "MONEYSPENT" -> client.moneySpent.toString()
                //"LINK" -> "https://partner.loyaltyplant.com/IPLPartner/#/reviews/${review.id.toString()}/info?processed=all&withComment=true"

                else -> it.value
            }
        }
    }


    private fun decodingBirthdayTemplates(rawMessage: String, employee: EmployeeModel): String {
        val employeeBirthdayValues = makeBirthdayValues(employee.birthday)
        val regex = "\\[/?.*?\\]".toRegex()
        return rawMessage.replace(regex) {
            when (it.value.uppercase().substring(1, it.value.length - 1)) {
                "BDCOUNTER" -> employeeBirthdayValues.bdCounter.toString()
                "BDDAY" -> employeeBirthdayValues.bdDay
                "BDMONTHWORD" -> employeeBirthdayValues.bdMonthWord
                "AGE" -> employeeBirthdayValues.newAge.toString()
                "AGEYEARWORD" -> employeeBirthdayValues.ageYearWord
                "BDYEAR" -> employeeBirthdayValues.bdYear
                "BDMONTH" -> employeeBirthdayValues.bdMonth
                "BDDATE" -> employeeBirthdayValues.bdDate
                "NAME" -> employee.name
                "ID" -> employee.id
                "FIRSTNAME" -> employee.firstName
                "MIDDLENAME" -> employee.middleName
                "LASTNAME" -> employee.lastName
                "PHONE" -> employee.phone
                "BIRTHDAY" -> convertDate(employee.birthday)
                "ADDRESS" -> employee.address
                "HIREDATE" -> convertDate(employee.hireDate)
                "LOGIN" -> employee.login
                "CELLPHONE" -> employee.cellPhone
                "NOTE" -> employee.note
                "CARDNUMBER" -> employee.cardNumber
                "CLIENT" -> employee.client
                "CODE" -> employee.code
                "DELETED" -> employee.deleted
                "DEPARTMENTCODES" -> employee.departmentCodes
                "EMPLOYEE" -> employee.employee
                "MAINROLECODE" -> employee.mainRoleCode
                "MAINROLEID" -> employee.mainRoleId
                "PREFERREDDEPARTMENTCODE" -> employee.preferredDepartmentCode
                "RESPONSIBILITYDEPARTMENTCODES" -> employee.responsibilityDepartmentCodes
                "SNILS" -> employee.snils
                "SUPPLIER" -> employee.supplier
                "TAXPAYERIDNUMBER" -> employee.taxpayerIdNumber
                "ROLECODES" -> employee.roleCodes.joinToString() //mutableListOf(),
                "ROLESIDS" -> employee.rolesIds.joinToString() //mutableListOf()
                else -> it.value
            }
        }
    }

    private fun convertDate(rawDate: String): String = if (rawDate != "") {
        try {
            ZonedDateTime.parse(
                if (rawDate.length == 10) rawDate + "T00:00:00.000Z" else rawDate
            ).toLocalDate()
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        } catch (e: Exception) {
            ""
        }
    } else ""

    private fun convertDateTimeWithZone(rawDate: String, timeZone: String): String = if (rawDate != "") {
        try {
            ZonedDateTime
                .parse(rawDate)
                .withZoneSameInstant(ZoneId.of(timeZone))
                .toLocalDateTime()
                .format(DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy"))
        } catch (e: Exception) {
            ""
        }
    } else ""

    private fun convertDateTime(rawDate: String): String = if (rawDate != "") {
        try {
            ZonedDateTime
                .parse(rawDate)
                .toLocalDateTime()
                .format(DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy"))
        } catch (e: Exception) {
            ""
        }
    } else ""

    private fun toStarsString(rating: Int?): String = rating?.let { "⭐".repeat(it) } ?: ""

}