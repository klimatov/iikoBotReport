package core

import data.repository.BotRepositoryImpl
import data.repository.ReportRepositoryImpl
import domain.usecases.MakeReportPostUseCase
import kotlinx.coroutines.*

class ReportManager(bot: Bot) {

    private val reportRepository by lazy {
        ReportRepositoryImpl
    }

    private val botRepository by lazy {
        BotRepositoryImpl(bot = bot)
    }

    private val makeReportPostUseCase by lazy {
        MakeReportPostUseCase(reportRepository = reportRepository, botRepository = botRepository)
    }

    suspend fun start() {
            while (true) {
                println("process...")
                makeReportPostUseCase.execute()
                delay(30000L)
            }
    }

}