package webServer

import core.WorkersManager
import data.NameIdBundleRepository
import data.ReportsRepository
import domain.usecases.GetReportList
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
import models.ReportWorkerParam
import models.WorkerParam
import models.WorkerState
import utils.Logging
import java.io.File
import java.util.*

fun Application.configureEditWorker(workersManager: WorkersManager) {
    val tag = "configureEditWorker"
    routing {
        authenticate("auth-basic") {

            static("/") {
                staticRootFolder = File("")
                files("css")
            }

            get("/edit-worker") {
                val nameIdBundleList = NameIdBundleRepository.get()
                val workerList = ReportsRepository().get()
                val newWorkerId = UUID.randomUUID().toString()
                var editWorkerParam = ReportWorkerParam(
                    workerParam = WorkerParam(
                        sendChatId = listOf(), // ok
                        sendWhenType = 1,
                        sendPeriod = 5,
                        sendTime = listOf("10:00"),
                        sendWeekDay = listOf(),
                        sendMonthDay = listOf(),
                        nameInHeader = true,
                        workerId = newWorkerId, // ok
                        workerName = "Отчет-${newWorkerId.takeLast(12)}", // ok
                        sendDateTimeList = listOf()
                    ),
                    reportId = "REPORT_ID", // ok
                    reportPeriod = 0, //ok
                    messageHeader = true, //ok
                    messageSuffix = mapOf(Pair(-1, " шт.")), // ok
                    messageAmount = 0, // ok
                    messageWordLimit = mapOf(Pair(-1, 1))
                )
                val workerId = call.request.queryParameters["workerId"]
                if (workerList.containsKey(workerId)) editWorkerParam = workerList[workerId]!!

                call.respondHtml(HttpStatusCode.OK) {
                    head {
                        title {
                            +"iikoBotReport edit worker"
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

                            workerIdField(editWorkerParam.workerParam.workerId, editWorkerParam.workerParam.workerIsActive, workerTypeName = "Отчет")

                            workerNameField(editWorkerParam.workerParam.workerName, editWorkerParam.workerParam.nameInHeader, workerTypeName = "отчета")

//!!!!! ---------------------------------------------------------------------------------------------------------------
                            p(classes = "field half") {
                                label(classes = "label") {
                                    +"Название отчета в iiko"
                                }
                                select(classes = "select") {
                                    name = "reportId"
                                    id = "reportId"
                                    GetReportList().execute().forEach { (reportId, reportName) ->
                                        option {
                                            value = reportId
                                            selected = editWorkerParam.reportId == value
                                            +reportName
                                        }
                                    }
                                }
                            }
                            p(classes = "field half") {
                                input(type = InputType.checkBox, classes = "checkbox-input") {
                                    checked = true
                                }
                                label(classes = "checkbox-label") {
                                    onClick = "function setOrderName() {\n" +
                                            "var labelOption = document.getElementById('reportId');  \n" +
                                            "document.getElementById('workerName').value=labelOption.options[labelOption.selectedIndex].label;}\n" +
                                            "setOrderName();"
                                    +"Копировать текст в название отчета"
                                }
                            }

//!!!!! ---------------------------------------------------------------------------------------------------------------
                            p(classes = "field half") {
                                label(classes = "label") {
                                    +"Период для отчета из iiko" //(0 - сегодня, n - количество дней, -1 с начала недели, -2 с начала месяца, -3 с начала квартала, -4 с начала года)
                                }
                                select(classes = "select") {
                                    name = "reportPeriodType"
                                    option {
                                        value = "0"
                                        selected = editWorkerParam.reportPeriod.toString() == value
                                        +"Сегодня (текущий день)"
                                    }
                                    option {
                                        value = "-1"
                                        selected = editWorkerParam.reportPeriod.toString() == value
                                        +"С начала недели"
                                    }
                                    option {
                                        value = "-2"
                                        selected = editWorkerParam.reportPeriod.toString() == value
                                        +"С начала месяца"
                                    }
                                    option {
                                        value = "-3"
                                        selected = editWorkerParam.reportPeriod.toString() == value
                                        +"С начала квартала"
                                    }
                                    option {
                                        value = "-4"
                                        selected = editWorkerParam.reportPeriod.toString() == value
                                        +"С начала года"
                                    }
                                    option {
                                        value = "1"
                                        selected = editWorkerParam.reportPeriod > 0
                                        +"Количество дней ->"
                                    }
                                }
                            }
                            p(classes = "field half") {
                                label(classes = "label") {
                                    +"Количество дней"
                                }
                                input(
                                    type = InputType.number,
                                    name = "reportPeriodQuantity",
                                    classes = "text-input required"
                                ) {
                                    required = true
                                    min = "0"
                                    max = "999"
                                    value =
                                        if (editWorkerParam.reportPeriod > 0) editWorkerParam.reportPeriod.toString() else "0"
                                }
                            }
//!!!!! ---------------------------------------------------------------------------------------------------------------

                            sendChatIdField(editWorkerParam.workerParam.sendChatId, nameIdBundleList)

                            sendWhenTypeField(editWorkerParam.workerParam.sendWhenType, workerTypeName = "отчет")

                            sendPeriodField(editWorkerParam.workerParam.sendPeriod)

                            sendTimeField(editWorkerParam.workerParam.sendTime)

                            sendWeekDayField(editWorkerParam.workerParam.sendWeekDay)

                            sendMonthDay(editWorkerParam.workerParam.sendMonthDay)


                            sendDateField(editWorkerParam.workerParam.sendDateTimeList)

//!!!!! ---------------------------------------------------------------------------------------------------------------
                            p(classes = "field") {
                                label(classes = "label") {
                                    +"Отображать ли названия колонок в заголовке?"
                                }
                                select(classes = "select") {
                                    name = "messageHeader"
                                    option {
                                        value = "true"
                                        selected = editWorkerParam.messageHeader.toString() == value
                                        +"Да"
                                    }
                                    option {
                                        value = "false"
                                        selected = editWorkerParam.messageHeader.toString() == value
                                        +"Нет"
                                    }
                                }
                            }

//!!!!! ---------------------------------------------------------------------------------------------------------------
                            p(classes = "field half") {
                                label(classes = "label") {
                                    +"Суффикс "
                                }
                                select(classes = "select") {
                                    name = "messageSuffixText"
                                    option {
                                        label = "руб."
                                        value = " руб."
                                        selected = editWorkerParam.messageSuffix.values.first().toString() == value
                                    }
                                    option {
                                        label = "шт."
                                        value = " шт."
                                        selected = editWorkerParam.messageSuffix.values.first().toString() == value
                                    }
                                }
                            }

                            p(classes = "field half") {
                                label(classes = "label") {
                                    +"в колонке №"
                                }
                                input(type = InputType.number, name = "messageSuffixCol", classes = "text-input") {
                                    min = "0"
                                    max = "20"
                                    value = editWorkerParam.messageSuffix.keys.first()
                                        .toString().toInt().plus(1).toString()
                                }
                            }

//!!!!! ---------------------------------------------------------------------------------------------------------------
                            p(classes = "field half") {
                                label(classes = "label") {
                                    +"В колонке №"
                                }
                                input(type = InputType.number, name = "messageWordLimitCol", classes = "text-input") {
                                    min = "0"
                                    max = "20"
                                    value = editWorkerParam.messageWordLimit.keys.first()
                                        .toString().toInt().plus(1).toString()
                                    required = true
                                }
                            }

                            p(classes = "field half") {
                                label(classes = "label") {
                                    +"количество слов не более"
                                }
                                input(type = InputType.number, name = "messageWordLimitSum", classes = "text-input") {
                                    min = "1"
                                    max = "20"
                                    value = editWorkerParam.messageWordLimit.values.first().toString()
                                    required = true
                                }
                            }

//!!!!! ---------------------------------------------------------------------------------------------------------------
                            p(classes = "field half") {
                                label(classes = "label") {
                                    +"Доп. строка (ИТОГО) с суммой колонки № (0 если не выводим)"
                                }
                                input(type = InputType.number, name = "messageAmount", classes = "text-input") {
                                    min = "0"
                                    max = "20"
                                    value = editWorkerParam.messageAmount.toString()
                                }
                            }

                            bottomButtonsField()
                        }
                        script(type = "text/javascript", src = "js/main.js") {}
                        script(type = "text/javascript") {+"editOnLoad('${editWorkerParam.workerParam.sendWhenType}')"}
                    }
                }
            }

            post("/edit-worker") {
                val workerList = ReportsRepository().get()
                val receiveParam: Map<String, List<String>> = call.receiveParameters().toMap()
                Logging.d(tag, receiveParam.toString())
                val userIP = call.request.origin.remoteHost
                val userName = call.principal<UserIdPrincipal>()?.name

                val htmlWorkerParam = ReportWorkerParam(
                    workerParam = WorkerParam(
                        workerId = receiveParam["workerId"]?.joinToString() ?: "", // ok
                        workerName = receiveParam["workerName"]?.joinToString() ?: "",
                        sendChatId = receiveParam["sendChatId"]?.map { it.toLong() } ?: listOf(),
//                        sendChatId = receiveParam["sendChatId"]?.joinToString()?.toLong() ?: 0,
                        sendWhenType = receiveParam["sendWhenType"]?.joinToString()?.toInt() ?: 0,
                        sendPeriod = receiveParam["sendPeriod"]?.joinToString()?.toInt() ?: 1,
                        sendTime = listOf(receiveParam["sendTime"]?.joinToString() ?: ""),
                        sendWeekDay = receiveParam["sendWeekDay"]?.map { it.toInt() } ?: listOf(1),
                        sendMonthDay = receiveParam["sendMonthDay"]?.map { it.toInt() } ?: listOf(1),
                        nameInHeader = receiveParam["nameInHeader"]?.joinToString().toString() == "on",
                        workerIsActive = receiveParam["workerIsActive"]?.joinToString().toString() == "on",
                        sendDateTimeList = receiveParam["sendDateTime"]?.filter { it != "2000-01-01T00:00"} ?: listOf(),
                    ),
                    reportId = receiveParam["reportId"]?.joinToString() ?: "",
                    reportPeriod = (if ((receiveParam["reportPeriodType"]?.joinToString()?.toInt()
                            ?: 0) > 0
                    ) receiveParam["reportPeriodQuantity"]?.joinToString()
                        ?.toInt() else receiveParam["reportPeriodType"]?.joinToString()?.toInt()) ?: 0,
                    messageHeader = receiveParam["messageHeader"]?.joinToString().toBoolean(),
                    messageSuffix = mapOf(
                        Pair(
                            receiveParam["messageSuffixCol"]?.joinToString()?.toInt()?.minus(1) ?: 0,
                            receiveParam["messageSuffixText"]?.joinToString()
                        ) as Pair<Int, String>
                    ),
                    messageAmount = receiveParam["messageAmount"]?.joinToString()?.toInt() ?: 0,
                    messageWordLimit = mapOf(
                        Pair(
                            receiveParam["messageWordLimitCol"]?.joinToString()?.toInt()?.minus(1) ?: 0,
                            receiveParam["messageWordLimitSum"]?.joinToString()?.toInt()
                        ) as Pair<Int, Int>
                    ),
                )

                if (receiveParam.containsKey("deleteButton")) {                                 // - DELETE !!!
                    Logging.i(
                        tag,
                        "User $userName [$userIP] pressed button DELETE for worker ${htmlWorkerParam.workerParam.workerName} - ${htmlWorkerParam.workerParam.workerId}"
                    )
                    workerList.remove(htmlWorkerParam.workerParam.workerId)
                    ReportsRepository().delete(htmlWorkerParam.workerParam.workerId)
//                    reportManager.changeWorkersConfig()
                    workersManager.makeChangeWorker(
                        workerState = WorkerState.DELETE,
                        workerData = htmlWorkerParam
                    )
                }

                if (receiveParam.containsKey("saveButton")) {                                   // - SAVE !!!
                    Logging.i(
                        tag,
                        "User $userName [$userIP] pressed button SAVE for worker ${htmlWorkerParam.workerParam.workerName} - ${htmlWorkerParam.workerParam.workerId}"
                    )
                    workerList[htmlWorkerParam.workerParam.workerId] = htmlWorkerParam
                    ReportsRepository().set(workerList)
//                    reportManager.changeWorkersConfig()
                    workersManager.makeChangeWorker(
                        workerState = WorkerState.UPDATE,
                        workerData = htmlWorkerParam
                    )
                }
                call.respondRedirect("/")
            }
        }
    }
}

