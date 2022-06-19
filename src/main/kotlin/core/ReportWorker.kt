package core

import data.repository.BotRepositoryImpl
import data.repository.ReportRepositoryImpl
import domain.usecases.MakeReportPostUseCase
import kotlinx.coroutines.*
import kotlinx.coroutines.NonCancellable.isActive
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class ReportWorker(bot: Bot) {

    private val reportRepository by lazy {
        ReportRepositoryImpl
    }

    private val botRepository by lazy {
        BotRepositoryImpl(bot = bot)
    }

    private val makeReportPostUseCase by lazy {
        MakeReportPostUseCase(reportRepository = reportRepository, botRepository = botRepository)
    }

    suspend fun start(workerId: String) {
        while (isActive) {
            println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:MM:SS")) + " process worker $workerId...")
            makeReportPostUseCase.execute()
            delay(30000L)
        }
    }
}