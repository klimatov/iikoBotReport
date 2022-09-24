package webServer

import SecurityData.TELEGRAM_CHAT_ID
import core.ReportManager
import data.fileProcessing.RemindersRepository
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
import models.ReminderWorkerParam
import java.util.*
import utils.Logging
import java.io.File

fun Application.configureEditReminder(reportManager: ReportManager) {
    val tag = "configureEditReminder"
    routing {
        authenticate("auth-basic") {

            static("/") {
                staticRootFolder = File("")
                files("css")
            }

            get("/edit-reminder") {
                val reminderList = RemindersRepository().get()
                val newWorkerId = UUID.randomUUID().toString()
                var editReminderParam = ReminderWorkerParam(
                    workerId = newWorkerId, // ok
                    workerName = "Напоминание-${newWorkerId.take(8)}", // ok
                    reminderText = "", // текст напоминания
                    sendChatId = TELEGRAM_CHAT_ID, // ok
                    sendWhenType = 1,
                    sendPeriod = 5,
                    sendTime = listOf("10:00"),
                    sendWeekDay = listOf(),
                    sendMonthDay = listOf(),
                    nameInHeader = true
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

                            p(classes = "field") {
                                input(type = InputType.checkBox, name = "workerIsActive", classes = "checkbox-input") {
                                    checked = editReminderParam.workerIsActive
                                    id = "workerIsActive"
                                }
                                label(classes = "checkbox-label") {
                                    title = "Клик для включения/отключения активности напоминания"
                                    onClick = "function setCheckbox() {\n" +
                                            "var c = document.querySelector('#workerIsActive');\n" +
                                            "c.checked = !c.checked }\n" +
                                            "setCheckbox();"
                                    +"Напоминание с ID: ${editReminderParam.workerId} активно"
                                }
                            }
                            hiddenInput {
                                name = "workerId"
                                value = editReminderParam.workerId
                            }


                            p(classes = "field required half") {
                                label(classes = "label required") {
                                    +"Название напоминания"
                                }
                                input(type = InputType.text, name = "workerName", classes = "text-input") {
                                    value = editReminderParam.workerName
                                    required = true
                                    id = "workerName"
                                }
                            }
                            p(classes = "field half") {
                                input(type = InputType.checkBox, name = "nameInHeader", classes = "checkbox-input") {
                                    checked = editReminderParam.nameInHeader
                                    id = "nameInHeader"
                                }
                                label(classes = "checkbox-label") {
                                    title = "Клик для включения/отключения вывода названия в заголовке сообщения"
                                    onClick = "function setCheckbox() {\n" +
                                            "var c = document.querySelector('#nameInHeader');\n" +
                                            "c.checked = !c.checked }\n" +
                                            "setCheckbox();"
                                    +"Выводить в заголовке сообщения"
                                }
                            }

                            p(classes = "field required half") {
                                label(classes = "label required") {
                                    +"ID чата/юзера куда будет отправляться напоминание"
                                }
                                input(type = InputType.number, name = "sendChatId", classes = "text-input") {
                                    value = editReminderParam.sendChatId.toString()
                                    required = true
                                }
                            }


                            p(classes = "field half") {
                                label(classes = "label") {
                                    +"Когда отправлять напоминание" //"1 - периодически, 2 - дни недели, 3 - числа месяца, 0 - ежедневно"
                                }
                                select(classes = "select") {
                                    name = "sendWhenType"
                                    option {
//                                        label = "Периодически"
                                        value = "1"
                                        selected = editReminderParam.sendWhenType.toString() == value
                                        +"Периодически"
                                    }
                                    option {
//                                        label = "Ежедневно"
                                        value = "0"
                                        selected = editReminderParam.sendWhenType.toString() == value
                                        +"Ежедневно"
                                    }
                                    option {
//                                        label = "Дни недели"
                                        value = "2"
                                        selected = editReminderParam.sendWhenType.toString() == value
                                        +"Дни недели"
                                    }
                                    option {
//                                        label = "Числа месяца"
                                        value = "3"
                                        selected = editReminderParam.sendWhenType.toString() == value
                                        +"Числа месяца"
                                    }
                                }
                            }


                            p(classes = "field half") {
                                label(classes = "label") {
                                    +"Период отправки в минутах"
                                }
                                input(type = InputType.number, name = "sendPeriod", classes = "text-input") {
                                    min = "1"
                                    max = "1440"
                                    value = editReminderParam.sendPeriod.toString()
                                }
                            }


                            p(classes = "field half") {
                                label(classes = "label") {
                                    +"Время отправки (дни/недели/месяцы)"
                                }
                                input(type = InputType.time, name = "sendTime", classes = "text-input") {
                                    value = editReminderParam.sendTime.joinToString()
                                }
                            }


//!!!!! ---------------------------------------------------------------------------------------------------------------
                            div(classes = "field") {
                                label(classes = "label") {
                                    +"Дни недели для отправки напоминания"
                                }
                                ul(classes = "checkboxes") {
                                    val daysOfWeek = listOf(
                                        "Понедельник",
                                        "Вторник",
                                        "Среда",
                                        "Четверг",
                                        "Пятница",
                                        "Суббота",
                                        "Воскресенье"
                                    )

                                    for (day in 1..7) {
                                        li(classes = "checkbox") {
                                            input(
                                                type = InputType.checkBox,
                                                classes = "checkbox-input",
                                                name = "sendWeekDay"
                                            ) {
                                                value = day.toString()
                                                id = "sendWeekDay-${day}"
                                                checked = editReminderParam.sendWeekDay.toString().contains(value)
                                            }
                                            label(classes = "checkbox-label") {
                                                onClick = "function setCheckbox() {\n" +
                                                        "var c = document.querySelector('#sendWeekDay-${day}');\n" +
                                                        "c.checked = !c.checked }\n" +
                                                        "setCheckbox();"
                                                +daysOfWeek[day - 1]
                                            }
                                        }
                                    }
                                }
                            }
//!!!!! ---------------------------------------------------------------------------------------------------------------
                            div(classes = "field") {
                                label(classes = "label") {
                                    +"Числа месяца для отправки (32 - в последний день месяца)"
                                }
                                ul(classes = "checkboxes") {
                                    for (day in 1..32) {
                                        li(classes = "checkbox") {
                                            input(
                                                type = InputType.checkBox,
                                                classes = "checkbox-input",
                                                name = "sendMonthDay"
                                            ) {
                                                value = day.toString()
                                                id = "sendMonthDay-${day}"
                                                checked = editReminderParam.sendMonthDay.contains(value.toInt())
                                            }
                                            label(classes = "checkbox-label") {
                                                onClick = "function setCheckbox() {\n" +
                                                        "var c = document.querySelector('#sendMonthDay-${day}');\n" +
                                                        "c.checked = !c.checked }\n" +
                                                        "setCheckbox();"
                                                +if (day < 10) "0$day" else "$day"
                                            }
                                        }
                                    }
                                }
                            }
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

                            p(classes = "field half") {

                                ul(classes = "options") {

                                    li(classes = "option") {
                                        input(type = InputType.submit, classes = "button") {
                                            name = "saveButton"
                                            value = "Сохранить"
                                        }
                                    }

                                    li(classes = "option") {
                                        input(type = InputType.submit, classes = "button") {
                                            name = "deleteButton"
                                            value = "Удалить"
                                        }
                                    }

                                    li(classes = "option") {
                                        input(type = InputType.button, classes = "button") {
                                            name = "backButton"
                                            onClick = "history.back()"
                                            value = "Назад"
                                        }
                                    }
                                }
                            }

//!!!!! ---------------------------------------------------------------------------------------------------------------

                        }
                    }
                }
            }

            post("/edit-reminder") {
                val reminderList = RemindersRepository().get()
                val receiveParam: Map<String, List<String>> = call.receiveParameters().toMap()
                Logging.d(tag, receiveParam.toString())
                val userIP = call.request.origin.remoteHost
                val userName = call.principal<UserIdPrincipal>()?.name

//                Logging.d(tag, receiveParam.get("sendWeekDay").toString())
                val htmlReminderParam = ReminderWorkerParam(
                    workerId = receiveParam["workerId"]?.joinToString() ?: "", // ok
                    workerName = receiveParam["workerName"]?.joinToString() ?: "",
                    reminderText = receiveParam["reminderText"]?.joinToString() ?: "",
                    sendChatId = receiveParam["sendChatId"]?.joinToString()?.toLong() ?: 0,
                    sendWhenType = receiveParam["sendWhenType"]?.joinToString()?.toInt() ?: 0,
                    sendPeriod = receiveParam["sendPeriod"]?.joinToString()?.toInt() ?: 1,
                    sendTime = listOf(receiveParam["sendTime"]?.joinToString() ?: ""),
                    sendWeekDay = receiveParam["sendWeekDay"]?.map { it.toInt() } ?: listOf(1),
                    sendMonthDay = receiveParam["sendMonthDay"]?.map { it.toInt() } ?: listOf(1),
                    nameInHeader = receiveParam["nameInHeader"]?.joinToString().toString() == "on",
                    workerIsActive = receiveParam["workerIsActive"]?.joinToString().toString() == "on"
                )

                if (receiveParam.containsKey("deleteButton")) {                                 // - DELETE !!!
                    Logging.i(
                        tag,
                        "User $userName [$userIP] pressed button DELETE for worker ${htmlReminderParam.workerName} - ${htmlReminderParam.workerId}"
                    )
                    reminderList.remove(htmlReminderParam.workerId)
                    RemindersRepository().set(reminderList)
                    reportManager.changeWorkersConfig()
                }

                if (receiveParam.containsKey("saveButton")) {                                   // - SAVE !!!
                    Logging.i(
                        tag,
                        "User $userName [$userIP] pressed button SAVE for worker ${htmlReminderParam.workerName} - ${htmlReminderParam.workerId}"
                    )
                    reminderList.put(htmlReminderParam.workerId, htmlReminderParam)
                    RemindersRepository().set(reminderList)
                    reportManager.changeWorkersConfig()
                }
                call.respondRedirect("/")
            }
        }
    }
}
