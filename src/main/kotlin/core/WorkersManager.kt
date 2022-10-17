package core

import data.fileProcessing.ReportsRepository
import kotlinx.coroutines.*
import models.*
import utils.Logging

class WorkersManager(private val bot: Bot) {
    private val tag = this::class.java.simpleName
    private val scopesList: MutableMap<String, Job> = mutableMapOf()
    private var reportsList: MutableMap<String, ReportWorkerParam> = mutableMapOf()
    private var remindersList: MutableMap<String, ReminderWorkerParam> = mutableMapOf()
    private var activeWorkersList: MutableMap<String, WorkersParam> = mutableMapOf()

    suspend fun start() {
        reportsList = ReportsRepository().get() ?: mutableMapOf() // загружаем список репортов
        reportsList.forEach {
            if (it.value.workerIsActive) activeWorkersList[it.value.workerId] =
                WorkersParam(
                    workerId = it.value.workerId,
                    workerType = WorkerType.REPORT,
                    workerState = WorkerState.CREATE,
                    workerIsActive = it.value.workerIsActive
                ) // если активен, добавляем в список воркеров
        }

        processWorkers()
    }

    private suspend fun processWorkers() {
        activeWorkersList.forEach { worker ->
            when (worker.value.workerState) {
                WorkerState.CREATE -> createWorker(worker.value)
                WorkerState.DELETE -> deleteWorker(worker.value)
                WorkerState.UPDATE -> updateWorker(worker.value)
                else -> return@forEach
            }
        }
    }

    private suspend fun createWorker(workerParam: WorkersParam) { //если скоупа нет и воркер активен, то запускаем
        if ((!scopesList.containsKey(workerParam.workerId)) && (workerParam.workerIsActive)) {
            Logging.i(tag, "Появился новый worker ${workerParam.workerId}, ЗАПУСК")
            val scope = CoroutineScope(Dispatchers.Default).launch(CoroutineName(workerParam.workerId)) {

                when (workerParam.workerType) {
                    WorkerType.REPORT -> {
                        ReportWorker(bot = bot).process(reportsList[workerParam.workerId] ?: ReportWorkerParam())
                    }
                    WorkerType.REMINDER -> {}
                }
            }
            scope.start()
            scopesList[workerParam.workerId] = scope
            activeWorkersList[workerParam.workerId]?.workerState = WorkerState.WORK
        } else {
            Logging.e(
                tag, " worker ${workerParam.workerId} is already running or not active and NOT STARTED!"
            )
        }
    }

    private suspend fun updateWorker(workerParam: WorkersParam) {
        if (scopesList.containsKey(workerParam.workerId)) {
            Logging.i(tag, "Изменение конфигурации worker'а ${workerParam.workerId}, ОБРАБОТКА")
            cancelWorker(workerParam.workerId)
        }
        createWorker(workerParam)
    }

    private suspend fun deleteWorker(workerParam: WorkersParam) {
        Logging.i(tag, "В конфигурации удален worker ${workerParam.workerId}, УДАЛЕНИЕ")
        cancelWorker(workerParam.workerId)
        activeWorkersList[workerParam.workerId]?.workerState = WorkerState.DELETED
    }

    private suspend fun cancelWorker(workerId: String) {
        Logging.i(tag, "cancel worker ${workerId}...")
        scopesList[workerId]?.cancel()
        scopesList.remove(workerId)
    }

    suspend fun makeChangeWorker(workerState: WorkerState, workerData: Any) {
        when (workerData) {
            is ReportWorkerParam -> {
                activeWorkersList[workerData.workerId] = WorkersParam(
                    workerId = workerData.workerId,
                    workerType = WorkerType.REPORT,
                    workerState = workerState,
                    workerIsActive = workerData.workerIsActive
                )
                if (workerState == WorkerState.DELETE) reportsList.remove(workerData.workerId) else
                    reportsList[workerData.workerId] = workerData
            }
            is ReminderWorkerParam -> {}
        }
        processWorkers()
    }

}