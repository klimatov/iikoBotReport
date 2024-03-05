package core

import data.remoteAPI.BotRepositoryImpl
import data.remoteAPI.iiko.GetFromIikoApiRepositoryImpl
import data.remoteAPI.iiko.ReportRepositoryImpl
import data.remoteAPI.loyaltyPlant.GetFromLPApiRepositoryImpl
import data.remoteAPI.twoGis.GetFromTwoGisApiRepositoryImpl
import domain.models.*
import domain.usecases.*
import kotlinx.coroutines.NonCancellable.isActive
import kotlinx.coroutines.delay
import models.*
import utils.Logging
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class WorkerScope(bot: Bot) {
    private val tag = this::class.java.simpleName

    private val reportRepository by lazy {
        ReportRepositoryImpl
    }

    private val getFromIikoApiRepository by lazy {
        GetFromIikoApiRepositoryImpl()
    }

    private val getFromLPApiRepository by lazy {
        GetFromLPApiRepositoryImpl
    }

    private val getFromTwoGisApiRepository by lazy {
        GetFromTwoGisApiRepositoryImpl
    }

    private val botRepository by lazy {
        BotRepositoryImpl(bot = bot)
    }

    private val makeReportPostUseCase by lazy {
        MakeReportPostUseCase(reportRepository = reportRepository, botRepository = botRepository)
    }

    private val makeReminderPostUseCase by lazy {
        MakeReminderPostUseCase(botRepository = botRepository)
    }

    private val makeBirthdayPostUseCase by lazy {
        MakeBirthdayPostUseCase(getFromIikoApiRepository = getFromIikoApiRepository, botRepository = botRepository)
    }

    private val makeReviewsPostUseCase by lazy {
        MakeReviewsPostUseCase(getFromLPApiRepository = getFromLPApiRepository, botRepository = botRepository)
    }

    private val makeTwoGisPostUseCase by lazy {
        MakeTwoGisPostUseCase(getFromTwoGisApiRepository = getFromTwoGisApiRepository, botRepository = botRepository)
    }

    private var lastSendDate: LocalDate = LocalDate.parse("01.01.2000", DateTimeFormatter.ofPattern("dd.MM.yyyy"))
    private var firstStart: Boolean = true

    private var sendDateTimeMap: MutableMap<LocalDate, MutableList<String>> = mutableMapOf()

    private lateinit var workerType: WorkerType
    private lateinit var anyWorkerParam: Any

    suspend fun processReport(reportWorkerParam: ReportWorkerParam) {
        anyWorkerParam = reportWorkerParam
        workerType = WorkerType.REPORT
        process(reportWorkerParam.workerParam)
    }

    suspend fun processReminder(reminderWorkerParam: ReminderWorkerParam) {
        anyWorkerParam = reminderWorkerParam
        workerType = WorkerType.REMINDER
        process(reminderWorkerParam.workerParam)
    }

    suspend fun processBirthday(birthdayWorkerParam: BirthdayWorkerParam) {
        anyWorkerParam = birthdayWorkerParam
        workerType = WorkerType.BIRTHDAY
        process(birthdayWorkerParam.workerParam)
    }

    suspend fun processReviews(reviewsWorkerParam: ReviewsWorkerParam) {
        anyWorkerParam = reviewsWorkerParam
        workerType = WorkerType.REVIEWS
        process(reviewsWorkerParam.workerParam)
    }

    suspend fun processTwoGis(twoGisWorkerParam: TwoGisWorkerParam) {
        anyWorkerParam = twoGisWorkerParam
        workerType = WorkerType.TWOGIS
        process(twoGisWorkerParam.workerParam)
    }

    private suspend fun process(workerParam: WorkerParam) {
        firstStartInit(workerParam)
        while (isActive) {
            when (workerParam.sendWhenType) {
                1 -> sendPeriodical(workerParam)
                else -> sendOnSchedule(workerParam)
            }
        }
    }

    private fun firstStartInit(workerParam: WorkerParam) {
        val todayDT = LocalDateTime.now()
        val todayDate = todayDT.toLocalDate()

        Logging.d(tag, "[${workerParam.workerName}] datetime: ${workerParam.sendDateTimeList}")
        sendDateTimeMap = mutableMapOf()
        sendDateTimeMap = workerParam.sendDateTimeList
            .groupByTo(sendDateTimeMap, // группируем список даты-времени в мапу дата-список времени
                keySelector = {
                    LocalDateTime.parse(it).toLocalDate()
                }, // ключ в формате даты
                valueTransform = { it.substringAfter("T") }
            )
            .filter {// отфильтровываем прошедшие даты
                it.key >= todayDate
            }
            .toMutableMap()

        Logging.d(tag, "[${workerParam.workerName}] date filtered: $sendDateTimeMap")

        if (sendDateTimeMap.containsKey(todayDate)) { // отфильтровываем прошедшее сегодня время
            sendDateTimeMap[todayDate] = sendDateTimeMap[todayDate]?.filter {
                LocalTime.parse(it) >= todayDT.toLocalTime()
            }?.toMutableList() ?: mutableListOf()
        }
        Logging.d(tag, "[${workerParam.workerName}] datetime filtered: $sendDateTimeMap")
    }

    private suspend fun sendMessage() {
        when (workerType) {
            WorkerType.REPORT -> {
                makeReportPostUseCase.execute((anyWorkerParam as ReportWorkerParam).mapToReportParam())
            }

            WorkerType.REMINDER -> {
                makeReminderPostUseCase.execute((anyWorkerParam as ReminderWorkerParam).mapToReminderParam())
            }

            WorkerType.BIRTHDAY -> {
                makeBirthdayPostUseCase.execute((anyWorkerParam as BirthdayWorkerParam).mapToBirthdayParam())
            }

            WorkerType.REVIEWS -> {
                makeReviewsPostUseCase.execute((anyWorkerParam as ReviewsWorkerParam).mapToReviewsParam())
            }

            WorkerType.TWOGIS -> {
                makeTwoGisPostUseCase.execute((anyWorkerParam as TwoGisWorkerParam).mapToTwoGisParam())
            }
        }
    }

    private suspend fun sendPeriodical(workerParam: WorkerParam) {
        Logging.i(
            tag,
            "process worker [${workerParam.workerName}] - ${workerParam.workerId}..."
        )
        sendMessage()
        delay(workerParam.sendPeriod.toDuration(DurationUnit.MINUTES))
    }

    private suspend fun sendOnSchedule(workerParam: WorkerParam) {
        Logging.d(tag, "[${workerParam.workerName}] " + sendDateTimeMap.toString())

        val todayDT = LocalDateTime.now()
        val todayDate = todayDT.toLocalDate()
        var sendTime = if (workerParam.sendWhenType == 4)
            nextSendTime(todayDate)
        else workerParam.sendTime.first()

        Logging.d(tag, "[${workerParam.workerName}] send time:" + sendTime)

        var secToSendTime =
            LocalTime.parse(sendTime)
                .toSecondOfDay() // секунд с начала дня до времени отправки
        val secToNowTime = todayDT.toLocalTime().toSecondOfDay() // секунд с начала дня до текущего момента
        Logging.d(
            tag,
            "[${workerParam.workerName}] с 00:00 до времени отправки: ${secToSendTime}s, c 00:00 до текущего времени: ${secToNowTime}s"
        )

        if (                                    // когда отправлять: 0 - ежедневно, 2 - дни недели, 3 - числа месяца, 4 - в даты
            ((lastSendDate != todayDate) && (workerParam.sendWhenType == 0)) ||
            ((lastSendDate != todayDate) && (workerParam.sendWhenType == 2 && workerParam.sendWeekDay.contains(todayDT.dayOfWeek.value))) ||
            ((lastSendDate != todayDate) && (workerParam.sendWhenType == 3 && workerParam.sendMonthDay.contains(todayDT.dayOfMonth))) ||
            ((lastSendDate != todayDate) && (workerParam.sendWhenType == 3 && ( // если по числам и содержит 32, то отправляем в последний день месяца
                    workerParam.sendMonthDay.contains(32) &&
                            todayDT.dayOfMonth == todayDT.month.length(isLeapYear(todayDT.year)))
                    )) ||
            ((workerParam.sendWhenType == 4) && (sendDateTimeMap.containsKey(todayDate)))
        ) {
            val timeSend = sendTime.split(":")
            if ((todayDT.hour == timeSend[0].toInt() && todayDT.minute >= timeSend[1].toInt()) || (todayDT.hour > timeSend[0].toInt())) {
                // если (час = и минуты >=) или час > то выполняем

                if (!firstStart) { // не отправляем если это первый запуск после сохранения
                    Logging.i(
                        tag,
                        "process worker [${workerParam.workerName}] - ${workerParam.workerId}..."
                    )
                    sendMessage()
                    if (workerParam.sendWhenType == 4) {
                        sendDateTimeMap[todayDate]?.remove(sendTime) // удаляем отправленное время из списка
                        if (sendDateTimeMap[todayDate]?.isEmpty() == true) { // если сегодняшний список пуст, то удаляем запись
                            sendDateTimeMap.remove(todayDate)
                        }
                        sendTime = nextSendTime(todayDate)
                        secToSendTime =
                            LocalTime.parse(sendTime)
                                .toSecondOfDay() // секунд с начала дня до времени отправки
                    }
                }

                lastSendDate = todayDate
            } else {
                Logging.d(tag, "[${workerParam.workerName}] Рано отправлять")
            }
        }

        firstStart = false
        var delayTime: Long = 0
        if (secToNowTime >= secToSendTime) delayTime = ((86400 - secToNowTime) + secToSendTime).toLong() * 1000
        else delayTime = (secToSendTime - secToNowTime).toLong() * 1000
        Logging.d(tag, "[${workerParam.workerName}] worker ${workerParam.workerId} DELAY ${delayTime / 1000}s")
        delay(delayTime)
    }

    private fun nextSendTime(todayDate: LocalDate): String =
        (
                if (sendDateTimeMap.containsKey(todayDate)) sendDateTimeMap[todayDate]?.minOrNull() // если есть сегодня, то берем самое раннее время
                else sendDateTimeMap // иначе ищем следующую дату и берем самое раннее время
                    .filter {// отфильтровываем прошедшие даты
                        it.key > todayDate
                    }
                    .entries.sortedBy { it.key }
                    .first()
                    .value
                    .minOrNull()
                ) ?: "00:00:00" // если список пуст или вернулся null то 00:00:00

    private fun isLeapYear(year: Int): Boolean = year % 4 == 0 && year % 100 != 0 || year % 400 == 0
}