package webServer.plugins

import SecurityData.TELEGRAM_CHAT_ID
import core.ReportManager
import data.fileProcessing.WorkersRepository
import domain.usecases.GetReportList
import io.ktor.http.*
import io.ktor.server.html.*
import kotlinx.html.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.util.*
import models.WorkerParam
import java.util.*

fun Application.configureEditWorker(reportManager: ReportManager) {

    routing {
        authenticate("auth-basic") {
            get("/edit-worker") {
                val workerList = WorkersRepository().get()
                val newWorkerId = UUID.randomUUID().toString()
                var editWorkerParam = WorkerParam(
                    workerId = newWorkerId, // ok
                    workerName = "Отчет-${newWorkerId.take(8)}", // ok
                    reportId = "REPORT_ID", // ok
                    reportPeriod = 0, //ok
                    sendChatId = TELEGRAM_CHAT_ID, // ok
                    sendWhenType = 1,
                    sendPeriod = 5,
                    sendTime = listOf("10:00"),
                    sendWeekDay = listOf(1, 4),
                    sendMonthDay = listOf(1, 15),
                    messageHeader = false, //ok
                    messageSuffix = mapOf(Pair(10, " шт.")), // ok
                    messageAmount = 0, // ok
                    messageWordLimit = mapOf(Pair(0, 0))
                )
                val workerId = call.request.queryParameters["workerId"]
                if (workerList?.containsKey(workerId) == true) editWorkerParam = workerList[workerId]!!

                call.respondHtml(HttpStatusCode.OK) {
                    head {
                        title {
                            +"iikoBotReport edit worker"
                        }
                    }
                    body {
                        postForm {
                            +"ID отчета (worker'а): ${editWorkerParam.workerId}"
                            hiddenInput {
                                name = "workerId"
                                value = editWorkerParam.workerId
                            }
                            repeat(2) { br() }

                            +"Название отчета: "
                            input(type = InputType.text, name = "workerName") {
                                value = editWorkerParam.workerName
                                required = true
                            }
                            repeat(2) { br() }

                            +"ID отчета в iiko: "

                            select {
                                name = "reportId"
                                GetReportList().execute().forEach { reportId, reportName ->
                                    option {
                                        label = reportName
                                        value = reportId
                                        selected = editWorkerParam.reportId == value
                                    }
                                }
                            }

//                            input(type = InputType.text, name = "reportId") {
//                                value = editWorkerParam.reportId
//                                required = true
//                            }
                            repeat(2) { br() }

                            +"Период данных для формирования отчета из iiko: " //(0 - сегодня, n - количество дней, -1 с начала недели, -2 с начала месяца, -3 с начала квартала, -4 с начала года)
                            select {
                                name = "reportPeriodType"
                                option {
                                    label = "Сегодня (текущий день)"
                                    value = "0"
                                    selected = editWorkerParam.reportPeriod.toString() == value
                                }
                                option {
                                    label = "С начала недели"
                                    value = "-1"
                                    selected = editWorkerParam.reportPeriod.toString() == value
                                }
                                option {
                                    label = "С начала месяца"
                                    value = "-2"
                                    selected = editWorkerParam.reportPeriod.toString() == value
                                }
                                option {
                                    label = "С начала квартала"
                                    value = "-3"
                                    selected = editWorkerParam.reportPeriod.toString() == value
                                }
                                option {
                                    label = "С начала года"
                                    value = "-4"
                                    selected = editWorkerParam.reportPeriod.toString() == value
                                }
                                option {
                                    label = "Количество дней ->"
                                    value = "1"
                                    selected = editWorkerParam.reportPeriod > 0
                                }
                            }
                            +" количество дней: "
                            input(
                                type = InputType.number,
                                name = "reportPeriodQuantity"
                            ) {
                                if (editWorkerParam.reportPeriod > 0) value =
                                    editWorkerParam.reportPeriod.toString() else value = "0"
                            }
                            repeat(2) { br() }

                            +"ID чата/юзера куда будет отправлятся отчет: "
                            input(type = InputType.number, name = "sendChatId") {
                                value = editWorkerParam.sendChatId.toString()
                                required = true
                            }
                            repeat(2) { br() }

                            +"Когда отправлять отчет: " //"1 - периодически, 2 - дни недели, 3 - числа месяца, 0 - отчет не отправляем"
                            select {
                                name = "sendWhenType"
                                option {
                                    label = "Периодически"
                                    value = "1"
                                    selected = editWorkerParam.sendWhenType.toString() == value
                                }
                                option {
                                    label = "Дни недели"
                                    value = "2"
                                    selected = editWorkerParam.sendWhenType.toString() == value
                                }
                                option {
                                    label = "Числа месяца"
                                    value = "3"
                                    selected = editWorkerParam.sendWhenType.toString() == value
                                }
                                option {
                                    label = "Отчет не отправляем"
                                    value = "0"
                                    selected = editWorkerParam.sendWhenType.toString() == value
                                }
                            }
                            span {
                                style = "color:red;font-size:smaller;font-style: italic;"
                                +" *Пока реализован только периодический"
                            }

                            repeat(2) { br() }

                            +"Период отправки в минутах: "
                            input(type = InputType.number, name = "sendPeriod") {
                                value = editWorkerParam.sendPeriod.toString()
                            }
                            repeat(2) { br() }

                            +"Время отправки (для еженедельного/ежемесячного отчета): "
                            input(type = InputType.time, name = "sendTime") {
                                value = editWorkerParam.sendTime.joinToString()
                            }
                            span {
                                style = "color:red;font-size:smaller;font-style: italic;"
                                +" *Пока не реализовано"
                            }
                            repeat(2) { br() }


                            +"Дни недели для отправки отчета: "
                            select {
                                name = "sendWeekDay"
                                multiple = true
                                option {
                                    label = "Понедельник"
                                    value = "1"
                                    selected = editWorkerParam.sendWeekDay.toString().contains(value)
                                }
                                option {
                                    label = "Вторник"
                                    value = "2"
                                    selected = editWorkerParam.sendWeekDay.toString().contains(value)
                                }
                                option {
                                    label = "Среда"
                                    value = "3"
                                    selected = editWorkerParam.sendWeekDay.toString().contains(value)
                                }
                                option {
                                    label = "Четверг"
                                    value = "4"
                                    selected = editWorkerParam.sendWeekDay.toString().contains(value)
                                }
                                option {
                                    label = "Пятница"
                                    value = "5"
                                    selected = editWorkerParam.sendWeekDay.toString().contains(value)
                                }
                                option {
                                    label = "Суббота"
                                    value = "6"
                                    selected = editWorkerParam.sendWeekDay.toString().contains(value)
                                }
                                option {
                                    label = "Воскресенье"
                                    value = "7"
                                    selected = editWorkerParam.sendWeekDay.toString().contains(value)
                                }
                            }
                            +" (держим CTRL для нескольких)"
                            span {
                                style = "color:red;font-size:smaller;font-style: italic;"
                                +" *Пока не реализовано"
                            }
                            repeat(2) { br() }

                            +"Числа месяца для отправки отчета (32 - отправлять в последний день месяца): "
                            select {
                                name = "sendMonthDay"
                                multiple = true
                                for (day in 1..32) {
                                    option {
                                        label = day.toString()
                                        value = day.toString()
                                        selected = editWorkerParam.sendMonthDay.contains(value.toInt())
                                    }
                                }
                            }
                            +" (держим CTRL для нескольких)"
                            span {
                                style = "color:red;font-size:smaller;font-style: italic;"
                                +" *Пока не реализовано"
                            }
                            repeat(2) { br() }

                            +"Отображать ли заголовок в отчете?: "
                            select {
                                name = "messageHeader"
                                option {
                                    label = "Да"
                                    value = "true"
                                    selected = editWorkerParam.messageHeader.toString() == value
                                }
                                option {
                                    label = "Нет"
                                    value = "false"
                                    selected = editWorkerParam.messageHeader.toString() == value
                                }
                            }
                            repeat(2) { br() }

                            +"Суффикс "
                            select {
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
                            +" в колонке номер: "
                            input(type = InputType.number, name = "messageSuffixCol") {
                                value = editWorkerParam.messageSuffix.keys.first()
                                    .toString().toInt().plus(1).toString()
                            }
                            repeat(2) { br() }

                            +"Доп. строка (ИТОГО) с суммой колонки номер: "
                            input(type = InputType.number, name = "messageAmount") {
                                value = editWorkerParam.messageAmount.toString()
                            }
                            +" (0 если не выводим)"
                            repeat(2) { br() }

                            +"В колонке номер: "
                            input(type = InputType.number, name = "messageWordLimitCol") {
                                value = editWorkerParam.messageWordLimit.keys.first()
                                    .toString().toInt().plus(1).toString()
                                required = true
                            }
                            +" количество слов не более "
                            input(type = InputType.number, name = "messageWordLimitSum") {
                                value = editWorkerParam.messageWordLimit.values.first().toString()
                                required = true
                            }
                            +" (0 если не применяем)"
                            repeat(2) { br() }

//                        span {
//                            style = "color:red;font-size:large;font-weight:bold;"
//                            +errorMessage
//                        }
//                        br()

                            button(type = ButtonType.submit) {
                                name = "saveButton"
                                +"СОХРАНИТЬ"
                            }
                            +"  "
                            button(type = ButtonType.submit) {
                                name = "deleteButton"
                                +"УДАЛИТЬ"
                            }
                            +"  "
                            button(type = ButtonType.button) {
                                name = "backButton"
                                onClick = "history.back()"
                                +"НАЗАД"
                            }

                        }
                    }
                }
            }

            post("/edit-worker") {
                val workerList = WorkersRepository().get()
                val receiveParam: Map<String, List<String>> = call.receiveParameters().toMap()
                println(receiveParam)
                val htmlWorkerParam = WorkerParam(
                    workerId = receiveParam["workerId"]?.joinToString() ?: "", // ok
                    workerName = receiveParam["workerName"]?.joinToString() ?: "",
                    reportId = receiveParam["reportId"]?.joinToString() ?: "",
                    reportPeriod = (if ((receiveParam["reportPeriodType"]?.joinToString()?.toInt()
                            ?: 0) > 0
                    ) receiveParam["reportPeriodQuantity"]?.joinToString()
                        ?.toInt() else receiveParam["reportPeriodType"]?.joinToString()?.toInt()) ?: 0,
                    sendChatId = receiveParam["sendChatId"]?.joinToString()?.toLong() ?: 0,
                    sendWhenType = receiveParam["sendWhenType"]?.joinToString()?.toInt() ?: 0,
                    sendPeriod = receiveParam["sendPeriod"]?.joinToString()?.toInt() ?: 1,
                    sendTime = listOf(receiveParam["sendTime"]?.joinToString() ?: ""),
                    sendWeekDay = receiveParam["sendWeekDay"]?.map { it.toInt() } ?: listOf(1),
                    sendMonthDay = receiveParam["sendMonthDay"]?.map { it.toInt() } ?: listOf(1),
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
                    println("DELETE")
                    workerList?.remove(htmlWorkerParam.workerId)
                    WorkersRepository().set(workerList)
                    reportManager.changeWorkersConfig()
                }

                if (receiveParam.containsKey("saveButton")) {                                   // - SAVE !!!
                    println("SAVE")
                    workerList?.put(htmlWorkerParam.workerId, htmlWorkerParam)
                    WorkersRepository().set(workerList)
//                    reportManager.addWorker(htmlWorkerParam)
                    reportManager.changeWorkersConfig()
                }
                call.respondRedirect("/")
            }
        }
    }
}
