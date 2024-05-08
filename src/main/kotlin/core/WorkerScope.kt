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
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.random.Random
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

    /**
     * Мапа где: ключ: дата, значение: список стрингов времени отправки в эту дату
     */
    private val sendDateTimeMap: MutableMap<LocalDate, MutableList<String>> = mutableMapOf()

    private lateinit var workerType: WorkerType
    private lateinit var anyWorkerParam: Any

    private var readyToSendLists = ReadyToSendLists(WorkerParam(), sendDateTimeMap)

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
        readyToSendLists.workerParam = workerParam

        Logging.d(tag, "[${workerParam.workerName}] load all datetime: ${workerParam.sendDateTimeList}")
        sendDateTimeMap.clear()

        sendDateTimeMap.putAll(workerParam.sendDateTimeList
            .groupByTo(HashMap(), // группируем список даты-времени в мапу дата-список времени
                keySelector = {
                    LocalDateTime.parse(it).toLocalDate()
                }, // ключ в формате даты
                valueTransform = { it.substringAfter("T") }
            )
            .filter {// отфильтровываем прошедшие даты
                it.key >= todayDate
            }
            .toMutableMap()
        )

        Logging.d(tag, "[${workerParam.workerName}] date filtered: $sendDateTimeMap")

        if (sendDateTimeMap.containsKey(todayDate)) { // отфильтровываем прошедшее сегодня время в sendDateTimeMap
            sendDateTimeMap[todayDate] = sendDateTimeMap[todayDate]?.filter {
                LocalTime.parse(it) >= todayDT.toLocalTime()
            }?.toMutableList() ?: mutableListOf()
        }

        // отфильтровываем прошедшее время в todayTimeList
        val filterTime: List<SendTime> = readyToSendLists.todayTimeList
            .filter { it.time < todayDT.toLocalTime() }
        filterTime.forEach {
            readyToSendLists.todayTimeList.remove(it)
        }

        Logging.d(tag, "[${workerParam.workerName}] datetime filtered: $sendDateTimeMap")
    }

    private suspend fun sendMessage(preliminary: Boolean = false) {
        when (workerType) {
            WorkerType.REPORT -> {
                makeReportPostUseCase.execute((anyWorkerParam as ReportWorkerParam).mapToReportParam())
            }

            WorkerType.REMINDER -> {
                makeReminderPostUseCase.execute((anyWorkerParam as ReminderWorkerParam).mapToReminderParam())
            }

            WorkerType.BIRTHDAY -> {
                makeBirthdayPostUseCase.execute((anyWorkerParam as BirthdayWorkerParam).mapToBirthdayParam(), preliminary)
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

        val nowDateTime = LocalDateTime.now()

        val sendTime: SendTime? = readyToSendLists.todayTimeList.minOrNull()
        var nextSendDateTime: LocalDateTime? = null

        Logging.d(tag, "[${workerParam.workerName}] sendTime: $sendTime")

        if (sendTime != null) { // есть время отправки сегодня

            if (sendTime.time <= nowDateTime.toLocalTime()) { // если время отправки прошло или равно текущему (пора отправлять)
                when { // когда отправлять: 0 - ежедневно, 2 - дни недели, 3 - числа месяца, 4 - в даты

                    sendTime.preliminarySendBeforeDays > 0 -> doSend(workerParam, true) // если предотправка

                    workerParam.sendWhenType == 0 -> doSend(workerParam) // 0 - ежедневно

                    workerParam.sendWhenType == 2 && workerParam.sendWeekDay.contains(nowDateTime.dayOfWeek.value) -> // 2 - дни недели
                        doSend(workerParam)

                    workerParam.sendWhenType == 3 && workerParam.sendMonthDay.contains(nowDateTime.dayOfMonth) -> // 3 - числа месяца
                        doSend(workerParam)

                    workerParam.sendWhenType == 3 && ( // если по числам и содержит 32, то отправляем в последний день месяца
                            workerParam.sendMonthDay.contains(32) &&
                                    nowDateTime.dayOfMonth == nowDateTime.month.length(isLeapYear(nowDateTime.year))) ->
                        doSend(workerParam)

                    (workerParam.sendWhenType == 4) && sendDateTimeMap.containsKey(nowDateTime.toLocalDate()) -> // 4 - в даты
                        doSend(workerParam)

                    else -> { // если сегодня не отправляем, но время в списке есть

                    }
                }
                readyToSendLists.todayTimeList.remove(sendTime) // удаляем отправленное время
                nextSendDateTime = nowDateTime // ставим текущее DT

            } else { // рано отправлять
                nextSendDateTime = nowDateTime.with(sendTime.time)
            }

        } else { // т.к. был null (сегодня нет времени отправки)
            nextSendDateTime = nowDateTime.plusDays(1).with(            // ставим завтрашний день
                readyToSendLists.tomorrowTimeList.minOrNull()?.time // берем время из завтра
                    ?: LocalTime.parse("00:00").plusNanos(Random.nextLong(2000000000)) // если завтра нет отправки, то ставим 00:00 + рандом до 2сек
            )
        }

        var delayTime = try {
            Duration.between(nowDateTime, nextSendDateTime).toMillis()
        } catch (e: Exception) {
            Logging.e(
                tag,
                "[${workerParam.workerName}] Не удалось корректно вычислить разницу во времени для задержки! Exception: $e"
            )
            -1
        }

        Logging.i(
            tag,
            "[${workerParam.workerName}] ${workerParam.workerId} sendTime: $sendTime, nowDateTime: $nowDateTime, nextSendTime: $nextSendDateTime, calculate delay = ${delayTime / 1000}s"
        )
        if (delayTime < 0) {
            Logging.e(tag, "[${workerParam.workerName}] delayTime < 0 and = $delayTime ms!!! Set 100 ms")
            delayTime = 100
        }

        delay(delayTime + 50)
    }

    private suspend fun doSend(workerParam: WorkerParam, preliminary: Boolean = false) {
        Logging.i(tag, "process worker [${workerParam.workerName}] - ${workerParam.workerId}...")
        sendMessage(preliminary)
    }

    private fun isLeapYear(year: Int): Boolean = year % 4 == 0 && year % 100 != 0 || year % 400 == 0
}

