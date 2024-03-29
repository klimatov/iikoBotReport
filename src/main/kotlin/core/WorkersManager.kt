package core

import data.*
import data.remoteAPI.BotRepositoryImpl
import data.remoteAPI.iiko.GetFromIikoApiRepositoryImpl
import data.remoteAPI.iiko.ReportRepositoryImpl
import domain.models.mapToBirthdayParam
import domain.models.mapToReminderParam
import domain.models.mapToReportParam
import domain.usecases.MakeBirthdayPostUseCase
import domain.usecases.MakeReminderPostUseCase
import domain.usecases.MakeReportPostUseCase
import kotlinx.coroutines.*
import models.*
import utils.Logging

class WorkersManager(private val bot: Bot) {
    private val tag = this::class.java.simpleName
    private val scopesList: MutableMap<String, Job> = mutableMapOf()

    private var reportsList: MutableMap<String, ReportWorkerParam> = mutableMapOf()
    private var remindersList: MutableMap<String, ReminderWorkerParam> = mutableMapOf()
    private var birthdayList: MutableMap<String, BirthdayWorkerParam> = mutableMapOf()
    private var reviewsList: MutableMap<String, ReviewsWorkerParam> = mutableMapOf()
    private var twoGisReviewsList: MutableMap<String, TwoGisWorkerParam> = mutableMapOf()

    private var activeWorkersList: MutableMap<String, ActiveWorkerParam> = mutableMapOf()

    suspend fun start() {
        reportsList = ReportsRepository().get() ?: mutableMapOf() // загружаем список репортов
        reportsList.forEach {
            if (it.value.workerParam.workerIsActive) activeWorkersList[it.value.workerParam.workerId] =
                ActiveWorkerParam(
                    workerId = it.value.workerParam.workerId,
                    workerType = WorkerType.REPORT,
                    workerState = WorkerState.CREATE,
                    workerIsActive = it.value.workerParam.workerIsActive
                ) // если активен, добавляем в список воркеров
        }

        remindersList = RemindersRepository().get() ?: mutableMapOf() // загружаем список напоминаний
        remindersList.forEach {
            if (it.value.workerParam.workerIsActive) activeWorkersList[it.value.workerParam.workerId] =
                ActiveWorkerParam(
                    workerId = it.value.workerParam.workerId,
                    workerType = WorkerType.REMINDER,
                    workerState = WorkerState.CREATE,
                    workerIsActive = it.value.workerParam.workerIsActive
                ) // если активен, добавляем в список воркеров
        }

        birthdayList = BirthdayRepository().get() ?: mutableMapOf() // загружаем список напоминаний о ДР
        birthdayList.forEach {
            if (it.value.workerParam.workerIsActive) activeWorkersList[it.value.workerParam.workerId] =
                ActiveWorkerParam(
                    workerId = it.value.workerParam.workerId,
                    workerType = WorkerType.BIRTHDAY,
                    workerState = WorkerState.CREATE,
                    workerIsActive = it.value.workerParam.workerIsActive
                ) // если активен, добавляем в список воркеров
        }

        reviewsList = ReviewsRepository().get() ?: mutableMapOf() // загружаем список отчетов об отзывах
        reviewsList.forEach {
            if (it.value.workerParam.workerIsActive) activeWorkersList[it.value.workerParam.workerId] =
                ActiveWorkerParam(
                    workerId = it.value.workerParam.workerId,
                    workerType = WorkerType.REVIEWS,
                    workerState = WorkerState.CREATE,
                    workerIsActive = it.value.workerParam.workerIsActive
                ) // если активен, добавляем в список воркеров
        }

        twoGisReviewsList = TwoGisRepository().get() ?: mutableMapOf() // загружаем список отчетов 2gis
        twoGisReviewsList.forEach {
            if (it.value.workerParam.workerIsActive) activeWorkersList[it.value.workerParam.workerId] =
                ActiveWorkerParam(
                    workerId = it.value.workerParam.workerId,
                    workerType = WorkerType.TWOGIS,
                    workerState = WorkerState.CREATE,
                    workerIsActive = it.value.workerParam.workerIsActive
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

    private suspend fun createWorker(workerParam: ActiveWorkerParam) { //если скоупа нет и воркер активен, то запускаем
        if ((!scopesList.containsKey(workerParam.workerId)) && (workerParam.workerIsActive)) {
            Logging.i(tag, "Появился новый worker ${workerParam.workerId}, ЗАПУСК")
            val scope = CoroutineScope(Dispatchers.Default).launch(CoroutineName(workerParam.workerId)) {
                when (workerParam.workerType) {
                    WorkerType.REPORT -> {
                        WorkerScope(bot = bot).processReport(reportsList[workerParam.workerId] ?: ReportWorkerParam())
                    }
                    WorkerType.REMINDER -> {
                        WorkerScope(bot = bot).processReminder(remindersList[workerParam.workerId] ?: ReminderWorkerParam())
                    }
                    WorkerType.BIRTHDAY -> {
                        WorkerScope(bot = bot).processBirthday(birthdayList[workerParam.workerId] ?: BirthdayWorkerParam())
                    }
                    WorkerType.REVIEWS -> {
                        WorkerScope(bot = bot).processReviews(reviewsList[workerParam.workerId] ?: ReviewsWorkerParam())
                    }
                    WorkerType.TWOGIS -> {
                        WorkerScope(bot = bot).processTwoGis(twoGisReviewsList[workerParam.workerId] ?: TwoGisWorkerParam())
                    }
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

    private suspend fun updateWorker(workerParam: ActiveWorkerParam) {
        if (scopesList.containsKey(workerParam.workerId)) {
            Logging.i(tag, "Изменение конфигурации worker'а ${workerParam.workerId}, ОБРАБОТКА")
            cancelWorker(workerParam.workerId)
        }
        createWorker(workerParam)
    }

    private suspend fun deleteWorker(workerParam: ActiveWorkerParam) {
        Logging.i(tag, "В конфигурации удален worker ${workerParam.workerId}, УДАЛЕНИЕ")
        cancelWorker(workerParam.workerId)
        activeWorkersList[workerParam.workerId]?.workerState = WorkerState.DELETED
    }

    private suspend fun cancelWorker(workerId: String) {
        Logging.i(tag, "cancel worker ${workerId}...")
        scopesList[workerId]?.cancel()
        scopesList.remove(workerId)
    }

    suspend fun sendNowWorkerMessage(workerId: String): Boolean {
//        if (scopesList.containsKey(workerId)) {
//            scopesList[workerId].let {  }
//            return true
//        } else return false

        when {
            reportsList.containsKey(workerId) -> {
                reportsList[workerId]?.let {
                    MakeReportPostUseCase(ReportRepositoryImpl, BotRepositoryImpl(bot))
                        .execute(it.mapToReportParam())
                }
                return true
            }
            remindersList.containsKey(workerId) -> {
                remindersList[workerId]?.let {
                    MakeReminderPostUseCase(BotRepositoryImpl(bot))
                        .execute(it.mapToReminderParam())
                }
                return true
            }
            birthdayList.containsKey(workerId) -> {
                birthdayList[workerId]?.let {
                    MakeBirthdayPostUseCase(GetFromIikoApiRepositoryImpl(), BotRepositoryImpl(bot))
                        .execute(it.mapToBirthdayParam())
                }
                return true
            }
            else -> return false
        }


    }

    suspend fun makeChangeWorker(workerState: WorkerState, workerData: Any) {
        when (workerData) {
            is ReportWorkerParam -> {
                activeWorkersList[workerData.workerParam.workerId] = ActiveWorkerParam(
                    workerId = workerData.workerParam.workerId,
                    workerType = WorkerType.REPORT,
                    workerState = workerState,
                    workerIsActive = workerData.workerParam.workerIsActive
                )
                if (workerState == WorkerState.DELETE) reportsList.remove(workerData.workerParam.workerId) else
                    reportsList[workerData.workerParam.workerId] = workerData
            }
            is ReminderWorkerParam -> {
                activeWorkersList[workerData.workerParam.workerId] = ActiveWorkerParam(
                    workerId = workerData.workerParam.workerId,
                    workerType = WorkerType.REMINDER,
                    workerState = workerState,
                    workerIsActive = workerData.workerParam.workerIsActive
                )
                if (workerState == WorkerState.DELETE) remindersList.remove(workerData.workerParam.workerId) else
                    remindersList[workerData.workerParam.workerId] = workerData
            }
            is TwoGisWorkerParam -> {
                activeWorkersList[workerData.workerParam.workerId] = ActiveWorkerParam(
                    workerId = workerData.workerParam.workerId,
                    workerType = WorkerType.TWOGIS,
                    workerState = workerState,
                    workerIsActive = workerData.workerParam.workerIsActive
                )
                if (workerState == WorkerState.DELETE) twoGisReviewsList.remove(workerData.workerParam.workerId) else
                    twoGisReviewsList[workerData.workerParam.workerId] = workerData
            }
            is BirthdayWorkerParam -> {
                activeWorkersList[workerData.workerParam.workerId] = ActiveWorkerParam(
                    workerId = workerData.workerParam.workerId,
                    workerType = WorkerType.BIRTHDAY,
                    workerState = workerState,
                    workerIsActive = workerData.workerParam.workerIsActive
                )
                if (workerState == WorkerState.DELETE) birthdayList.remove(workerData.workerParam.workerId) else
                    birthdayList[workerData.workerParam.workerId] = workerData
            }
            is ReviewsWorkerParam -> {
                activeWorkersList[workerData.workerParam.workerId] = ActiveWorkerParam(
                    workerId = workerData.workerParam.workerId,
                    workerType = WorkerType.REVIEWS,
                    workerState = workerState,
                    workerIsActive = workerData.workerParam.workerIsActive
                )
                if (workerState == WorkerState.DELETE) reviewsList.remove(workerData.workerParam.workerId) else
                    reviewsList[workerData.workerParam.workerId] = workerData
            }
        }
        processWorkers()
    }

}