package core

import data.repository.BotRepositoryImpl
import data.repository.ReportRepositoryImpl
import domain.models.ReportParam
import domain.usecases.MakeReportPostUseCase
import kotlinx.coroutines.*
import kotlinx.coroutines.NonCancellable.isActive
import models.WorkerParam
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import utils.Logging

class ReportWorker(bot: Bot) {
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

    suspend fun start(workerParam: WorkerParam) {
        while (isActive) {
            Logging.i(tag,"process worker [${workerParam.workerName}] - ${workerParam.workerId}..."
            )
            makeReportPostUseCase.execute(mapToDomain(workerParam = workerParam))
            delay(workerParam.sendPeriod.toDuration(DurationUnit.MINUTES))
        }
    }

    private fun mapToDomain(workerParam: WorkerParam): ReportParam {
        return ReportParam(
            reportId = workerParam.reportId,
            reportPeriod = workerParam.reportPeriod,
            sendChatId = workerParam.sendChatId,
            messageHeader = workerParam.messageHeader,
            messageSuffix = workerParam.messageSuffix,
            messageAmount = workerParam.messageAmount,
            messageWordLimit = workerParam.messageWordLimit,
            nameInHeader = workerParam.nameInHeader,
            workerIsActive = workerParam.workerIsActive,
            workerName = workerParam.workerName
        )
    }
}