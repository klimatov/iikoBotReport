package webServer

import core.WorkersManager
import data.fileProcessing.BirthdayRepository
import data.fileProcessing.NameIdBundleRepository
import io.ktor.http.*
import io.ktor.server.html.*
import kotlinx.html.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.util.*
import models.BirthdayWorkerParam
import models.WorkerParam
import models.WorkerState
import java.util.*
import utils.Logging
import java.io.File

fun Application.configureEditBirthday(workersManager: WorkersManager) {
    val tag = "configureEditBirthday"
    routing {
        authenticate("auth-basic") {

            static("/") {
                staticRootFolder = File("")
                files("css")
            }

            get("/edit-birthday") {
                val nameIdBundleList = NameIdBundleRepository.get()
                val birthdayList = BirthdayRepository().get()
                val newWorkerId = UUID.randomUUID().toString()
                var editBirthdayParam = BirthdayWorkerParam(
                    workerParam = WorkerParam(
                        workerId = newWorkerId, // ok
                        workerName = "Напоминание о ДР -${newWorkerId.take(8)}", // ok
                        sendChatId = listOf(), // ok
                        sendWhenType = 1,
                        sendPeriod = 5,
                        sendTime = listOf("10:00"),
                        sendWeekDay = listOf(),
                        sendMonthDay = listOf(),
                        nameInHeader = true,
                        sendDateTimeList = listOf()
                    ),
                    birthdayText = "" // текст напоминания
                )
                val workerId = call.request.queryParameters["workerId"]
                if (birthdayList.containsKey(workerId) == true) editBirthdayParam = birthdayList[workerId]!!

                call.respondHtml(HttpStatusCode.OK) {
                    head {
                        title {
                            +"iikoBotReport edit birthday reminder"
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

                            workerIdField(editBirthdayParam.workerParam.workerId, editBirthdayParam.workerParam.workerIsActive, workerTypeName = "Напоминание о ДР")

                            workerNameField(editBirthdayParam.workerParam.workerName, editBirthdayParam.workerParam.nameInHeader, workerTypeName = "напоминания о ДР")

                            sendChatIdField(editBirthdayParam.workerParam.sendChatId, nameIdBundleList)

                            sendWhenTypeField(editBirthdayParam.workerParam.sendWhenType, workerTypeName = "напоминание о ДР")

                            sendPeriodField(editBirthdayParam.workerParam.sendPeriod)

                            sendTimeField(editBirthdayParam.workerParam.sendTime)

                            sendWeekDayField(editBirthdayParam.workerParam.sendWeekDay)

                            sendMonthDay(editBirthdayParam.workerParam.sendMonthDay)


                            sendDateField(editBirthdayParam.workerParam.sendDateTimeList)
//                            sendDateField(listOf("2023-07-20T23:22", "2022-07-20T01:22"))

//!!!!! ---------------------------------------------------------------------------------------------------------------
                            p(classes = "field required") {
                                label(classes = "label") {
                                    br()
                                    +"Текст напоминания о ДР"
                                }
                                textArea(classes = "textarea") {
                                    name = "birthdayText"
                                    rows = "20"
                                    cols = "80"
                                    required = true
                                    +editBirthdayParam.birthdayText
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

            post("/edit-birthday") {
                val birthdayList = BirthdayRepository().get()
                val receiveParam: Map<String, List<String>> = call.receiveParameters().toMap()
                Logging.d(tag, receiveParam.toString())
                val userIP = call.request.origin.remoteHost
                val userName = call.principal<UserIdPrincipal>()?.name
                val htmlBirthdayParam = BirthdayWorkerParam(
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
                        sendDateTimeList = (receiveParam["sendDateTime"]?.filter { it != "2000-01-01T00:00"} ?: listOf()),
                    ),
                    birthdayText = receiveParam["birthdayText"]?.joinToString() ?: ""
                )

                if (receiveParam.containsKey("deleteButton")) {                                 // - DELETE !!!
                    Logging.i(
                        tag,
                        "User $userName [$userIP] pressed button DELETE for worker ${htmlBirthdayParam.workerParam.workerName} - ${htmlBirthdayParam.workerParam.workerId}"
                    )
                    birthdayList.remove(htmlBirthdayParam.workerParam.workerId)
                    BirthdayRepository().set(birthdayList)
                    workersManager.makeChangeWorker(
                        workerState = WorkerState.DELETE,
                        workerData = htmlBirthdayParam
                    )
                }

                if (receiveParam.containsKey("saveButton")) {                                   // - SAVE !!!
                    Logging.i(
                        tag,
                        "User $userName [$userIP] pressed button SAVE for worker ${htmlBirthdayParam.workerParam.workerName} - ${htmlBirthdayParam.workerParam.workerId}"
                    )
                    birthdayList.put(htmlBirthdayParam.workerParam.workerId, htmlBirthdayParam)
                    BirthdayRepository().set(birthdayList)
                    workersManager.makeChangeWorker(
                        workerState = WorkerState.UPDATE,
                        workerData = htmlBirthdayParam
                    )
                }
                call.respondRedirect("/")
            }
        }
    }
}










