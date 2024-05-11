package webServer

import core.WorkersManager
import data.NameIdBundleRepository
import data.RemindersRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import kotlinx.html.*
import models.ReminderWorkerParam
import models.WorkerParam
import models.WorkerState
import utils.Logging
import java.io.File
import java.util.*

fun Application.configureEditReminder(workersManager: WorkersManager) {
    val tag = "configureEditReminder"
    routing {
        authenticate("auth-basic") {

            static("/") {
                staticRootFolder = File("")
                files("css")
            }

            get("/edit-reminder") {
                val nameIdBundleList = NameIdBundleRepository.get()
                val reminderList = RemindersRepository().get()
                val newWorkerId = UUID.randomUUID().toString()
                var editReminderParam = ReminderWorkerParam(
                    workerParam = WorkerParam(
                        workerId = newWorkerId, // ok
                        workerName = "Напоминание-${newWorkerId.take(8)}", // ok
                        sendChatId = listOf(), // ok
                        sendWhenType = 1,
                        sendPeriod = 5,
                        sendTime = listOf("10:00"),
                        sendWeekDay = listOf(),
                        sendMonthDay = listOf(),
                        nameInHeader = true,
                        sendDateTimeList = listOf(),
                        preliminarySendTime = "10:00",
                        preliminarySendBeforeDays = 0,
                    ),
                    reminderText = "\n" +
                            "\n" +
                            "Шаблоны замены:\n" +
                            "[RANDOM] - Генерирует случайный набор из 6 цифр и символов\n" +
                            "[RANDOM4] - Генерирует случайный набор из 4 цифр" // текст напоминания
                )
                val workerId = call.request.queryParameters["workerId"]
                if (reminderList.containsKey(workerId) == true) editReminderParam = reminderList[workerId]!!

                call.respondHtml(HttpStatusCode.OK) {
                    head {
                        title {
                            +"iikoBotReport edit reminder"
                        }
                        meta {
                            name = "viewport"
                            content = "width=device-width, initial-scale=1"
                        }
                        link(
                            rel = "stylesheet",
                            href = "https://cdnjs.cloudflare.com/ajax/libs/normalize/5.0.0/normalize.min.css"
                        )
                        link(rel = "stylesheet", href = "main.css")
                    }
                    body {
                        postForm(classes = "form") {

                            workerIdField(
                                editReminderParam.workerParam.workerId,
                                editReminderParam.workerParam.workerIsActive,
                                workerTypeName = "Напоминание"
                            )

                            workerNameField(
                                editReminderParam.workerParam.workerName,
                                editReminderParam.workerParam.nameInHeader,
                                workerTypeName = "напоминания"
                            )

                            sendChatIdField(editReminderParam.workerParam.sendChatId, nameIdBundleList)

                            sendWhenTypeField(
                                editReminderParam.workerParam.sendWhenType,
                                workerTypeName = "напоминание"
                            )

                            sendPeriodField(editReminderParam.workerParam.sendPeriod)

                            sendTimeField(editReminderParam.workerParam.sendTime)

                            sendWeekDayField(editReminderParam.workerParam.sendWeekDay)

                            sendMonthDay(editReminderParam.workerParam.sendMonthDay)

                            sendDateField(editReminderParam.workerParam.sendDateTimeList)

                            preliminarySendTimeField(editReminderParam.workerParam.preliminarySendTime)

                            preliminarySendBeforeDays(
                                editReminderParam.workerParam.preliminarySendBeforeDays,
                                "события"
                            )


//!!!!! ---------------------------------------------------------------------------------------------------------------
                            p(classes = "field required") {
                                label(classes = "label") {
                                    br()
                                    +"Текст напоминания"
                                }
                                textArea(classes = "textarea") {
                                    name = "reminderText"
                                    rows = "20"
                                    cols = "80"
                                    required = true
                                    +editReminderParam.reminderText
                                }
                            }
//!!!!! ---------------------------------------------------------------------------------------------------------------

                            bottomButtonsField()

                        }
                        script(type = "text/javascript", src = "js/main.js") {}
                        script(type = "text/javascript") { +"editOnLoad()" }
                    }
                }
            }

            post("/edit-reminder") {
                val reminderList = RemindersRepository().get()
                val receiveParam: Map<String, List<String>> = call.receiveParameters().toMap()
                Logging.d(tag, receiveParam.toString())
                val userIP = call.request.origin.remoteHost
                val userName = call.principal<UserIdPrincipal>()?.name
                val htmlReminderParam = ReminderWorkerParam(
                    workerParam = WorkerParam(workerId = receiveParam["workerId"]?.joinToString() ?: "", // ok
                        workerName = receiveParam["workerName"]?.joinToString() ?: "",
                        sendChatId = receiveParam["sendChatId"]?.map { it.toLong() } ?: listOf(),
                        sendWhenType = receiveParam["sendWhenType"]?.joinToString()?.toInt() ?: 0,
                        sendPeriod = receiveParam["sendPeriod"]?.joinToString()?.toInt() ?: 1,
                        sendTime = listOf(receiveParam["sendTime"]?.joinToString() ?: ""),
                        sendWeekDay = receiveParam["sendWeekDay"]?.map { it.toInt() } ?: listOf(1),
                        sendMonthDay = receiveParam["sendMonthDay"]?.map { it.toInt() } ?: listOf(1),
                        nameInHeader = receiveParam["nameInHeader"]?.joinToString().toString() == "on",
                        workerIsActive = receiveParam["workerIsActive"]?.joinToString().toString() == "on",
                        sendDateTimeList = (receiveParam["sendDateTime"]?.filter { it != "2000-01-01T00:00" }
                            ?: listOf()),
                        preliminarySendBeforeDays = receiveParam["preliminarySendBeforeDays"]?.joinToString()?.toLong()
                            ?: 0,
                        preliminarySendTime = receiveParam["preliminarySendTime"]?.joinToString() ?: "10:00"
                    ),
                    reminderText = receiveParam["reminderText"]?.joinToString() ?: ""
                )

                when (htmlReminderParam.workerParam.sendWhenType) { //проверяем типы в которых разрешена предотправка
                    3, 4 -> {}
                    else -> htmlReminderParam.workerParam.preliminarySendBeforeDays = 0
                }

                if (receiveParam.containsKey("deleteButton")) {                                 // - DELETE !!!
                    Logging.i(
                        tag,
                        "User $userName [$userIP] pressed button DELETE for worker ${htmlReminderParam.workerParam.workerName} - ${htmlReminderParam.workerParam.workerId}"
                    )
                    reminderList.remove(htmlReminderParam.workerParam.workerId)
                    RemindersRepository().delete(htmlReminderParam.workerParam.workerId)
                    workersManager.makeChangeWorker(
                        workerState = WorkerState.DELETE,
                        workerData = htmlReminderParam
                    )
                }

                if (receiveParam.containsKey("saveButton")) {                                   // - SAVE !!!
                    Logging.i(
                        tag,
                        "User $userName [$userIP] pressed button SAVE for worker ${htmlReminderParam.workerParam.workerName} - ${htmlReminderParam.workerParam.workerId}"
                    )
                    reminderList.put(htmlReminderParam.workerParam.workerId, htmlReminderParam)
                    RemindersRepository().set(reminderList)
                    workersManager.makeChangeWorker(
                        workerState = WorkerState.UPDATE,
                        workerData = htmlReminderParam
                    )
                }
                call.respondRedirect("/")
            }
        }
    }
}










