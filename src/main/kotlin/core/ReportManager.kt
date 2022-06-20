package core

import SecurityData.REPORT_ID
import SecurityData.TELEGRAM_CHAT_ID
import kotlinx.coroutines.*
import models.WorkerParam
import java.util.*

class ReportManager(private val bot: Bot) {
    private val jobList: MutableMap<String, Job> = mutableMapOf()
    private val workerList: MutableList<WorkerParam> = mutableListOf()
    suspend fun start() {

        val testWorkerParam = WorkerParam(
            workerId = UUID.randomUUID().toString(), // ok
            workerName = "Test report", // ok
            reportId = REPORT_ID, // ok
            reportPeriod = 0, //ok
            sendChatId = TELEGRAM_CHAT_ID, // ok
            sendWhenType = 1,
            sendPeriod = 1,
            messageHeader = false, //ok
            messageSuffix = mapOf(Pair(1," руб."),Pair(10," шт.")), // ok
            messageAmount = 0
        )
        addWorker(testWorkerParam)
    }

    suspend fun addWorker(workerParam: WorkerParam) {
        val scope = CoroutineScope(Dispatchers.Default).launch(CoroutineName(workerParam.workerId)) {
            ReportWorker(bot = bot).start(workerParam)
        }
        scope.start()
        jobList[workerParam.workerId] = scope
        workerList.add(workerParam)
    }

    suspend fun cancelWorker(workerId: String) {
        jobList[workerId]?.cancel()
        jobList.remove(workerId)
    }
}