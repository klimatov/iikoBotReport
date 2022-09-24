package core

import data.fileProcessing.ReportsRepository
import kotlinx.coroutines.*
import models.ReportWorkerParam
import utils.Logging

class ReportManager(private val bot: Bot) {
    private val tag = this::class.java.simpleName
    private val workerScopeList: MutableMap<String, Job> = mutableMapOf()
    private var workerList: MutableMap<String, ReportWorkerParam> = mutableMapOf()
    suspend fun start() {
        workerList = ReportsRepository().get() ?: mutableMapOf() // загружаем список воркеров
        workerList.forEach {
            if (it.value.workerIsActive) addWorker(it.value) // запускаем только если воркер активен
        }
    }
    suspend fun addWorker(workerParam: ReportWorkerParam) {
        if (!workerScopeList.containsKey(workerParam.workerId)) {
            Logging.i(tag,"start worker ${workerParam.workerId}...")
            val scope = CoroutineScope(Dispatchers.Default).launch(CoroutineName(workerParam.workerId)) {
                ReportWorker(bot = bot).process(workerParam)
            }
            scope.start()
            workerScopeList[workerParam.workerId] = scope
            workerList[workerParam.workerId] = workerParam
        } else {
            Logging.e(tag," worker ${workerParam.workerId} is already running and NOT STARTED!"
            )
        }
    }

    suspend fun cancelWorker(workerId: String) {
        Logging.i(tag,"cancel worker ${workerId}...")
        workerScopeList[workerId]?.cancel()
        workerScopeList.remove(workerId)
    }

    suspend fun changeWorkersConfig() {
        val oldWorkerList = workerList // сохранили список работающих воркеров
        workerList = ReportsRepository().get() ?: mutableMapOf() // получили актуальный список воркеров
        workerList.forEach {// перебираем актуальные воркеры
            if (oldWorkerList.containsKey(it.key) && it.value != oldWorkerList[it.key]) {
                            // если воркер есть в старом списке и конфиг воркера изменился - перезапускаем воркер
                Logging.i(tag,"Изменение конфигурации worker'а ${it.key}, ОБРАБОТКА")
                cancelWorker(it.key)
                if (it.value.workerIsActive) addWorker(it.value) // перезапуск только если воркер активен
                return@forEach
            }
            if (!oldWorkerList.containsKey(it.key)) { // если появился новый воркер - запускаем его
                Logging.i(tag,"Появился новый worker ${it.key}, ЗАПУСК")
                addWorker(it.value)
                return@forEach
            }
        }
        oldWorkerList.forEach {//перебираем старые воркеры
            if (!workerList.containsKey(it.key)) { // если воркер из старого списка отсутствует в новом, значит он удален
                Logging.i(tag,"В конфигурации удален worker ${it.key}, УДАЛЕНИЕ")
                cancelWorker(it.key) // отменяем его
            }
        }
    }
}