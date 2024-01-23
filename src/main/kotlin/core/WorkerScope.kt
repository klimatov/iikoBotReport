package core

import data.repository.BotRepositoryImpl
import data.repository.GetFromIikoApiRepositoryImpl
import data.repository.GetFromLPApiRepositoryImpl
import data.repository.ReportRepositoryImpl
import domain.models.BirthdayParam
import domain.models.ReminderParam
import domain.models.ReportParam
import domain.models.ReviewsParam
import domain.repository.GetFromLPApiRepository
import domain.usecases.MakeBirthdayPostUseCase
import domain.usecases.MakeReminderPostUseCase
import domain.usecases.MakeReportPostUseCase
import domain.usecases.MakeReviewsPostUseCase
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

    private var lastSendDate: String = "01.01.2000"
    private var firstStart: Boolean = true

    private var sendDateTimeMap: MutableMap<String, MutableList<String>> = mutableMapOf()

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
        val todayDate = todayDT.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))

        Logging.d(tag, "datetime: ${workerParam.sendDateTimeList}")
        sendDateTimeMap = mutableMapOf()
        sendDateTimeMap = workerParam.sendDateTimeList
            .groupByTo(sendDateTimeMap, // группируем список даты-времени в мапу дата-список времени
                keySelector = {
                    LocalDateTime.parse(it).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                }, // ключ в формате даты
                valueTransform = { it.substringAfter("T") }
            )
            .filter {// отфильтровываем прошедшие даты
                LocalDate.parse(it.key, DateTimeFormatter.ofPattern("dd.MM.yyyy")) >= LocalDate.now()
            }
            .toMutableMap()

        Logging.d(tag, "date filtered: $sendDateTimeMap")

        if (sendDateTimeMap.containsKey(todayDate)) { // отфильтровываем прошедшее сегодня время
            sendDateTimeMap[todayDate] = sendDateTimeMap[todayDate]?.filter {
                LocalTime.parse(it) >= LocalTime.parse(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")))
            }?.toMutableList() ?: mutableListOf()
        }
        Logging.d(tag, "datetime filtered: $sendDateTimeMap")
    }

    private suspend fun sendMessage() {
        when (workerType) {
            WorkerType.REPORT -> {
                makeReportPostUseCase.execute(mapReportToDomain(workerParam = anyWorkerParam as ReportWorkerParam))
            }

            WorkerType.REMINDER -> {
                makeReminderPostUseCase.execute(mapReminderToDomain(reminderWorkerParam = anyWorkerParam as ReminderWorkerParam))
            }

            WorkerType.BIRTHDAY -> {
                makeBirthdayPostUseCase.execute(mapBirthdayToDomain(birthdayWorkerParam = anyWorkerParam as BirthdayWorkerParam))
            }
            WorkerType.REVIEWS -> {
                makeReviewsPostUseCase.execute(mapReviewsToDomain(reviewsWorkerParam = anyWorkerParam as ReviewsWorkerParam))
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
        Logging.d(tag, sendDateTimeMap.toString())

        val todayDT = LocalDateTime.now()
        val todayDate = todayDT.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        var sendTime = workerParam.sendTime.first()

        if (workerParam.sendWhenType == 4) {
            sendTime = nextSendTime(todayDate, sendTime)
        }

        var secToSendTime =
            LocalTime.parse(sendTime)
                .toSecondOfDay() // секунд с начала дня до времени отправки
        val secToNowTime = LocalTime.now().toSecondOfDay() // секунд с начала дня до текущего момента
        Logging.d(
            tag,
            "[${workerParam.workerName}] с 00:00 до времени отправки: ${secToSendTime}s до текущего времени: ${secToNowTime}s"
        )

            if (                                    // когда отправлять: 0 - ежедневно, 2 - дни недели, 3 - числа месяца, 4 - в даты
                ((lastSendDate != todayDate)&&(workerParam.sendWhenType == 0)) ||
                ((lastSendDate != todayDate)&&(workerParam.sendWhenType == 2 && workerParam.sendWeekDay.contains(todayDT.dayOfWeek.value))) ||
                ((lastSendDate != todayDate)&&(workerParam.sendWhenType == 3 && workerParam.sendMonthDay.contains(todayDT.dayOfMonth))) ||
                ((lastSendDate != todayDate)&&(workerParam.sendWhenType == 3 && ( // если по числам и содержит 32, то отправляем в последний день месяца
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
                            sendTime = nextSendTime(todayDate, sendTime)
                            secToSendTime =
                                LocalTime.parse(sendTime)
                                    .toSecondOfDay() // секунд с начала дня до времени отправки
                        }
                    }

                    lastSendDate = todayDate
                } else {
                    Logging.d(tag, "Рано отправлять")
                }
            }

        firstStart = false
        var delayTime: Long = 0
        if (secToNowTime >= secToSendTime) delayTime = ((86400 - secToNowTime) + secToSendTime).toLong() * 1000
        else delayTime = (secToSendTime - secToNowTime).toLong() * 1000
        Logging.d(tag, "worker ${workerParam.workerId} DELAY ${delayTime / 1000}s")
        delay(delayTime)
    }

    private fun nextSendTime(todayDate: String, sendTime: String): String {
        var sendTime1 = sendTime
        if ((sendDateTimeMap.isEmpty()) || (!sendDateTimeMap.containsKey(todayDate))) { // если список дат пуст или сегодня не отправляем
            sendTime1 = "23:59:59"
        } else {
            sendTime1 = sendDateTimeMap[todayDate]?.minOrNull()
                ?: "23:59:59" // если список пуст, то 23:59 иначе ближайшее время
        }
        Logging.d(tag, sendTime1)
        return sendTime1
    }

    private fun mapReminderToDomain(reminderWorkerParam: ReminderWorkerParam): ReminderParam {
        return ReminderParam(
            sendChatId = reminderWorkerParam.workerParam.sendChatId,
            nameInHeader = reminderWorkerParam.workerParam.nameInHeader,
            workerName = reminderWorkerParam.workerParam.workerName,
            reminderText = reminderWorkerParam.reminderText
        )
    }

    private fun mapBirthdayToDomain(birthdayWorkerParam: BirthdayWorkerParam): BirthdayParam {
        return BirthdayParam(
            sendChatId = birthdayWorkerParam.workerParam.sendChatId,
            nameInHeader = birthdayWorkerParam.workerParam.nameInHeader,
            workerName = birthdayWorkerParam.workerParam.workerName,
            birthdayText = birthdayWorkerParam.birthdayText,
            sendBeforeDays = birthdayWorkerParam.sendBeforeDays
        )
    }

    private fun mapReviewsToDomain(reviewsWorkerParam: ReviewsWorkerParam): ReviewsParam {
        return ReviewsParam(
            sendChatId = reviewsWorkerParam.workerParam.sendChatId,
            nameInHeader = reviewsWorkerParam.workerParam.nameInHeader,
            workerName = reviewsWorkerParam.workerParam.workerName,
            reviewsText = reviewsWorkerParam.reviewsText,
            workerId = reviewsWorkerParam.workerParam.workerId
        )
    }

    private fun mapReportToDomain(workerParam: ReportWorkerParam): ReportParam {
        return ReportParam(
            reportId = workerParam.reportId,
            reportPeriod = workerParam.reportPeriod,
            sendChatId = workerParam.workerParam.sendChatId,
            messageHeader = workerParam.messageHeader,
            messageSuffix = workerParam.messageSuffix,
            messageAmount = workerParam.messageAmount,
            messageWordLimit = workerParam.messageWordLimit,
            nameInHeader = workerParam.workerParam.nameInHeader,
            workerIsActive = workerParam.workerParam.workerIsActive,
            workerName = workerParam.workerParam.workerName
        )
    }

    private fun isLeapYear(year: Int): Boolean = year % 4 == 0 && year % 100 != 0 || year % 400 == 0
}