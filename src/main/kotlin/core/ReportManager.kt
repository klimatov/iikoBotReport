package core

import data.fileProcessing.WorkersRepository
import kotlinx.coroutines.*
import models.WorkerParam
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ReportManager(private val bot: Bot) {
    private val workerScopeList: MutableMap<String, Job> = mutableMapOf()
    private var workerList: MutableMap<String, WorkerParam> = mutableMapOf()
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

        workerList = WorkersRepository().get() ?: mutableMapOf()
        workerList.forEach {
            addWorker(it.value)
        }
    }

    suspend fun addWorker(workerParam: WorkerParam) {
        if (!workerScopeList.containsKey(workerParam.workerId)) {
            val scope = CoroutineScope(Dispatchers.Default).launch(CoroutineName(workerParam.workerId)) {
                ReportWorker(bot = bot).start(workerParam)
            }
            scope.start()
            workerScopeList[workerParam.workerId] = scope
            workerList[workerParam.workerId] = workerParam
        } else {
            println(
                LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")) + " worker ${workerParam.workerId} is already running and NOT STARTED!"
            )
        }
    }

    suspend fun cancelWorker(workerId: String) {
        println(
            LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")) + " cancel worker ${workerId}..."
        )
        workerScopeList[workerId]?.cancel()
        workerScopeList.remove(workerId)
    }

    suspend fun changeWorkersConfig() {
        val oldWorkerList = workerList // сохранили список работающих воркеров
        workerList = WorkersRepository().get() ?: mutableMapOf() // получили актуальный список воркеров
        workerList.forEach {// перебираем актуальные воркеры
            if (oldWorkerList.containsKey(it.key) && it.value != oldWorkerList[it.key]) { // если конфиг воркера изменился - перезапускаем воркер
                println("Изменение конфигурации worker'а ${it.key}, ПЕРЕЗАПУСК")
                cancelWorker(it.key)
                addWorker(it.value)
                return@forEach
            }
            if (!oldWorkerList.containsKey(it.key)) { // если появился новый воркер - запускаем его
                println("Появился новый worker ${it.key}, ЗАПУСК")
                addWorker(it.value)
                return@forEach
            }
        }
        oldWorkerList.forEach {//перебираем старые воркеры
            if (!workerList.containsKey(it.key)) { // если воркер из старого списка отсутствует в новом, значит он удален
                println("В конфигурации удален worker ${it.key}, УДАЛЕНИЕ")
                cancelWorker(it.key) // отменяем его
            }
        }
    }
}