package core

import data.repository.BotRepositoryImpl
import data.repository.ReportRepositoryImpl
import domain.models.ReminderParam
import domain.models.ReportParam
import domain.usecases.MakeReminderPostUseCase
import domain.usecases.MakeReportPostUseCase
import kotlinx.coroutines.*
import kotlinx.coroutines.NonCancellable.isActive
import models.*
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import utils.Logging
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class WorkerScope(bot: Bot) {
    private val tag = this::class.java.simpleName

    private val reportRepository by lazy {
        ReportRepositoryImpl
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

    private var lastSendDate: String = "01.01.2000"
    private var firstStart: Boolean = true

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

    suspend fun process(workerParam: WorkerParam) {
        while (isActive) {
            when (workerParam.sendWhenType) {
                1 -> sendPeriodical(workerParam)
                else -> sendOnSchedule(workerParam)
            }
        }
    }

    private suspend fun sendMessage() {
        when (workerType) {
            WorkerType.REPORT -> {
                makeReportPostUseCase.execute(mapReportToDomain(workerParam = anyWorkerParam as ReportWorkerParam))
            }
            WorkerType.REMINDER -> {
                makeReminderPostUseCase.execute(mapReminderToDomain(reminderWorkerParam = anyWorkerParam as ReminderWorkerParam))
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
        val todayDT = LocalDateTime.now()
        val todayDate = todayDT.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        val secToSendTime =
            LocalTime.parse(workerParam.sendTime.first())
                .toSecondOfDay() // секунд с начала дня до времени отправки
        val secToNowTime = LocalTime.now().toSecondOfDay() // секунд с начала дня до текущего момента

        Logging.d(
            tag,
            "[${workerParam.workerName}] с 00:00 до времени отправки: ${secToSendTime}s до текущего времени: ${secToNowTime}s"
        )

        if (lastSendDate != todayDate) {
            if (                                    // когда отправлять: 0 - ежедневно, 2 - дни недели, 3 - числа месяца
                (workerParam.sendWhenType == 0) ||
                (workerParam.sendWhenType == 2 && workerParam.sendWeekDay.contains(todayDT.dayOfWeek.value)) ||
                (workerParam.sendWhenType == 3 && workerParam.sendMonthDay.contains(todayDT.dayOfMonth)) ||
                (workerParam.sendWhenType == 3 && ( // если по числам и содержит 32, то отправляем в последний день месяца
                        workerParam.sendMonthDay.contains(32) &&
                                todayDT.dayOfMonth == todayDT.month.length(isLeapYear(todayDT.year)))
                        )
            ) {
                val timeSend = workerParam.sendTime.first().split(":")
                if ((todayDT.hour == timeSend[0].toInt() && todayDT.minute >= timeSend[1].toInt()) || (todayDT.hour > timeSend[0].toInt())) {
                    // если (час = и минуты >=) или час > то выполняем

                    if (!firstStart) { // не отправляем если это первый запуск после сохранения
                        Logging.i(
                            tag,
                            "process worker [${workerParam.workerName}] - ${workerParam.workerId}..."
                        )
                        sendMessage()
                    }

                    lastSendDate = todayDate
                } else {
                    Logging.d(tag, "Рано отправлять")
                }
            }
        }
        firstStart = false
        var delayTime: Long = 0
        if (secToNowTime >= secToSendTime) delayTime = ((86400 - secToNowTime) + secToSendTime).toLong() * 1000
        else delayTime = (secToSendTime - secToNowTime).toLong() * 1000
        Logging.d(tag, "worker ${workerParam.workerId} DELAY ${delayTime/1000}s")
        delay(delayTime)
    }

    private fun mapReminderToDomain(reminderWorkerParam: ReminderWorkerParam): ReminderParam {
        return ReminderParam(
            sendChatId = reminderWorkerParam.workerParam.sendChatId,
            nameInHeader = reminderWorkerParam.workerParam.nameInHeader,
            workerName = reminderWorkerParam.workerParam.workerName,
            reminderText = reminderWorkerParam.reminderText
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