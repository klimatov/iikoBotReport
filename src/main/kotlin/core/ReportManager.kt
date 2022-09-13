package core

import SecurityData.REPORT_ID
import SecurityData.TELEGRAM_CHAT_ID
import data.fileProcessing.WorkersRepository
import kotlinx.coroutines.*
import models.WorkerParam
import java.util.*

class ReportManager(private val bot: Bot) {
    private val workerList: MutableMap<String, Job> = mutableMapOf()
    private val reportList: MutableList<WorkerParam> = mutableListOf()
    suspend fun start() {

//        val testWorkerParam = WorkerParam(
//            workerId = UUID.randomUUID().toString(), // ok
//            workerName = "Test report", // ok
//            reportId = REPORT_ID, // ok
//            reportPeriod = 0, //ok
//            sendChatId = TELEGRAM_CHAT_ID, // ok
//            sendWhenType = 1,
//            sendPeriod = 1,
//            sendTime = listOf("10:00"),
//            sendWeekDay = listOf(1),
//            sendMonthDay = listOf(1),
//            messageHeader = false, //ok
//            messageSuffix = mapOf(Pair(10, " руб."), Pair(3, " шт.")), // ok, начинаем с 0
//            messageAmount = 4 // ok, начинаем с 1
//        )
//        addWorker(testWorkerParam)

        val workerList = WorkersRepository().get()
        workerList?.forEach {
            addWorker(it.value)
        }
    }

    suspend fun addWorker(workerParam: WorkerParam) {
        val scope = CoroutineScope(Dispatchers.Default).launch(CoroutineName(workerParam.workerId)) {
            ReportWorker(bot = bot).start(workerParam)
        }
        scope.start()
        workerList[workerParam.workerId] = scope
        reportList.add(workerParam)
    }

    suspend fun cancelWorker(workerId: String) {
        workerList[workerId]?.cancel()
        workerList.remove(workerId)
    }
}