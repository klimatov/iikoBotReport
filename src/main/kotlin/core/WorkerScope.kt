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

//    private var lastSendDate: LocalDate = LocalDate.parse("01.01.2000", DateTimeFormatter.ofPattern("dd.MM.yyyy"))
//    private var firstStart: Boolean = true

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
        )
        Logging.d(tag, "[${workerParam.workerName}] date filtered: $sendDateTimeMap")

        if (sendDateTimeMap.containsKey(todayDate)) { // отфильтровываем прошедшее сегодня время в sendDateTimeMap
            sendDateTimeMap[todayDate] = sendDateTimeMap[todayDate]?.filter {
                LocalTime.parse(it) >= todayDT.toLocalTime()
            }?.toMutableList() ?: mutableListOf()
        }

        // отфильтровываем прошедшее время в todayTimeList
        val filterTime: List<LocalDateTime> = readyToSendLists.todayTimeList
            .keys
            .filter { it < todayDT }
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

        val todayDT = LocalDateTime.now()
        val todayDate = todayDT.toLocalDate()

        var nowSendTime = readyToSendLists.todayTimeList.minByOrNull { it.key }?.toPair()
        var nextSendTime = readyToSendLists.tomorrowTimeList.minByOrNull { it.key }?.toPair()
        Logging.d(tag, "[${workerParam.workerName}] nowSendTime: $nowSendTime, nextSendTime: $nextSendTime")

        if (nowSendTime != null) { // есть время отправки сегодня
            val result: Boolean =
                when { // когда отправлять: 0 - ежедневно, 2 - дни недели, 3 - числа месяца, 4 - в даты

                    nowSendTime.second > 0 -> doSend(nowSendTime, todayDT, workerParam) // если предотправка

                    workerParam.sendWhenType == 0 -> doSend(nowSendTime, todayDT, workerParam)

                    workerParam.sendWhenType == 2 && workerParam.sendWeekDay.contains(todayDT.dayOfWeek.value) ->
                        doSend(nowSendTime, todayDT, workerParam)

                    workerParam.sendWhenType == 3 && workerParam.sendMonthDay.contains(todayDT.dayOfMonth) ->
                        doSend(nowSendTime, todayDT, workerParam)

                    workerParam.sendWhenType == 3 && ( // если по числам и содержит 32, то отправляем в последний день месяца
                            workerParam.sendMonthDay.contains(32) &&
                                    todayDT.dayOfMonth == todayDT.month.length(isLeapYear(todayDT.year))) ->
                        doSend(nowSendTime, todayDT, workerParam)

                    (workerParam.sendWhenType == 4) && sendDateTimeMap.containsKey(todayDate) ->
                        doSend(nowSendTime, todayDT, workerParam)

                    else -> { // если сегодня не отправляем, но время в списке есть
                        readyToSendLists.todayTimeList.remove(nowSendTime.first)
                        nowSendTime = todayDT to 0 // ставим текущее DT
                        true
                    }
                }
            if (result) { // если отправили
                nextSendTime = takeNextDT(todayDT)
            } else { // если не отправили, т.е. рано отправлять
                nextSendTime = nowSendTime
                nowSendTime = todayDT to 0 // ставим текущее DT
            }
        } else {
            nowSendTime = todayDT to 0 // т.к. был null (сегодня нет времени отправки) ставим текущее DT
            if (nextSendTime == null) nextSendTime = todayDT.plusDays(1)
                .with(LocalTime.parse("00:00")) to 0 // если завтра нет отправки то ставим 00:00 завтра
        }

        var delayTime = try {
            Duration.between(nowSendTime.first, nextSendTime.first).toMillis()
        } catch (e: Exception) {
            Logging.e(tag, "[${workerParam.workerName}] Exception: $e")
            -1
        }

        Logging.i(
            tag,
            "[${workerParam.workerName}] ${workerParam.workerId} nowSendTime: $nowSendTime, nextSendTime: $nextSendTime, calculate delay = ${delayTime / 1000}s"
        )
        if (delayTime < 0) delayTime = 5000

        delay(delayTime + 50)
    }

    private fun takeNextDT(todayDT: LocalDateTime): Pair<LocalDateTime, Long> =
        readyToSendLists.todayTimeList.minByOrNull { it.key }?.toPair() // берем следующий сегодня
            ?: readyToSendLists.tomorrowTimeList.minByOrNull { it.key }?.toPair() // если пусто, то следующий завтра
            ?: (todayDT.plusDays(1)
                .with(LocalTime.parse("00:00")) to 0) // если завтра тоже пусто, то возвращаем 00:00 завтра


    private suspend fun doSend(
        sendDateTime: Pair<LocalDateTime, Long>,
        todayDT: LocalDateTime,
        workerParam: WorkerParam,
    ): Boolean {
        if (sendDateTime.first <= todayDT) {  // если (час = и минуты >=) или час > то выполняем
            Logging.i(tag, "process worker [${workerParam.workerName}] - ${workerParam.workerId}...")
            sendMessage()
            readyToSendLists.todayTimeList.remove(sendDateTime.first)
            return true
        } else {
            Logging.d(tag, "[${workerParam.workerName}] Рано отправлять")
            return false
        }
    }

    /*    private fun nextSendTime(todayDate: LocalDate): String =
            (
                    if (sendDateTimeMap.containsKey(todayDate)) sendDateTimeMap[todayDate]?.minOrNull() // если есть сегодня, то берем самое раннее время
                    else sendDateTimeMap // иначе ищем следующую дату и берем самое раннее время
                        .filter {// отфильтровываем прошедшие даты
                            it.key > todayDate
                        }
                        .entries.sortedBy { it.key }
                        .firstOrNull()
                        ?.value
                        ?.minOrNull()
                    ) ?: "00:00:00" // если список пуст или вернулся null то 00:00:00*/

    private fun isLeapYear(year: Int): Boolean = year % 4 == 0 && year % 100 != 0 || year % 400 == 0
}

