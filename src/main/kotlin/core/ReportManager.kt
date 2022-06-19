package core

import kotlinx.coroutines.*
import models.WorkerPref

class ReportManager(val bot: Bot) {
    val workerList: MutableMap<String, Job> = mutableMapOf()
    suspend fun start() {

        repeat(3) {
            addWorker(WorkerPref(workerId = (it + 1).toString()))
        }

        delay(3000L)
        cancelWorker("3")
    }

    suspend fun addWorker(workerPref: WorkerPref) {
        val scope = CoroutineScope(Dispatchers.Default).launch(CoroutineName(workerPref.workerId)) {
            ReportWorker(bot = bot).start(workerId = workerPref.workerId)
        }
        scope.start()
        workerList[workerPref.workerId] = scope
    }

    suspend fun cancelWorker(workerId: String) {
        workerList[workerId]?.cancel()
        workerList.remove(workerId)
    }
}