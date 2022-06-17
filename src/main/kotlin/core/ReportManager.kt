package core

import domain.usecases.MakeReportPostUseCase
import kotlinx.coroutines.*

class ReportManager {

    private val makeReportPostUseCase by lazy {
        MakeReportPostUseCase()
    }

    suspend fun start() {
            while (true) {
                println("process...")
                makeReportPostUseCase.execute()
                delay(5000L)
            }
    }

}