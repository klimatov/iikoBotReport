package webServer

import core.WorkersManager
import data.BirthdayRepository
import data.NameIdBundleRepository
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
import models.BirthdayWorkerParam
import models.WorkerParam
import models.WorkerState
import utils.Logging
import java.io.File
import java.util.*

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
                        sendWhenType = 0, //0 - ежедневно
                        sendTime = listOf("10:00"),
                        nameInHeader = false,
                        sendDateTimeList = listOf(),
                        preliminarySendTime = "10:00",
                        preliminarySendBeforeDays = 0,
                    ),
                    birthdayText = "[firstName] [lastName] скоро празднует день рождения \uD83C\uDF89 " +
                            " (осталось дней: [BDCOUNTER]) - " +
                            "[bdDay] [bdMonthWord] исполняется [age] [ageYearWord]!!!\uD83E\uDD73\n" +
                            "\n" +
                            "Шаблоны замены:\n" +
                            "[BDCOUNTER] - сколько дней осталось до ДР\n" +
                            "[BIRTHDAY] - Дата ДР\n" +
                            "[BDDATE] - Дата ДР в этом году\n" +
                            "[BDDAY] - День ДР\n" +
                            "[BDMONTH] - Месяц ДР цифрой\n" +
                            "[BDMONTHWORD] - Месяц ДР словом\n" +
                            "[BDYEAR] - Год рождения\n" +
                            "[AGE] - Возраст (сколько исполняется)\n" +
                            "[AGEYEARWORD] - Слово лет/год/года\n" +
                            "[NAME] - Имя целиком (с кодом)\n" +
                            "[FIRSTNAME] - Имя\n" +
                            "[MIDDLENAME] - Отчество\n" +
                            "[LASTNAME] - Фамилия\n" +
                            "[PHONE] - Телефон\n" +
                            "[ADDRESS] - Адрес сотрудника\n" +
                            "[HIREDATE] - Дата трудоустройства\n" +
                            "[MAINROLECODE] - Занятость основная\n" +
                            "[ROLECODES] - Занятости все (списком)\n" +
                            "[SNILS] - СНИЛС\n" +
                            "[TAXPAYERIDNUMBER] - ИНН", // текст напоминания
                    sendBeforeDays = 0 // за сколько дней до ДР начать оповещать
                )
                val workerId = call.request.queryParameters["workerId"]
                if (birthdayList.containsKey(workerId)) editBirthdayParam = birthdayList[workerId]!!

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

                            workerIdField(
                                editBirthdayParam.workerParam.workerId,
                                editBirthdayParam.workerParam.workerIsActive,
                                workerTypeName = "Напоминание о ДР"
                            )

                            workerNameField(
                                editBirthdayParam.workerParam.workerName,
                                editBirthdayParam.workerParam.nameInHeader,
                                workerTypeName = "напоминания о ДР"
                            )

                            sendChatIdField(editBirthdayParam.workerParam.sendChatId, nameIdBundleList)

                            sendTimeField(editBirthdayParam.workerParam.sendTime)

                            sendBeforeDays(editBirthdayParam.sendBeforeDays)

                            preliminarySendTimeField(editBirthdayParam.workerParam.preliminarySendTime)

                            preliminarySendBeforeDays(editBirthdayParam.workerParam.preliminarySendBeforeDays, "ДР")

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
                        sendWhenType = 0,
                        sendPeriod = 1,
                        sendTime = listOf(receiveParam["sendTime"]?.joinToString() ?: ""),
                        sendWeekDay = listOf(1),
                        sendMonthDay = listOf(1),
                        nameInHeader = receiveParam["nameInHeader"]?.joinToString().toString() == "on",
                        workerIsActive = receiveParam["workerIsActive"]?.joinToString().toString() == "on",
                        sendDateTimeList = (listOf()),
                        preliminarySendBeforeDays = receiveParam["preliminarySendBeforeDays"]?.joinToString()?.toLong()
                            ?: 0,
                        preliminarySendTime = receiveParam["preliminarySendTime"]?.joinToString() ?: "10:00"
                    ),
                    birthdayText = receiveParam["birthdayText"]?.joinToString() ?: "",
                    sendBeforeDays = receiveParam["sendBeforeDays"]?.joinToString()?.toLong() ?: 0,
                )

                if (receiveParam.containsKey("deleteButton")) {                                 // - DELETE !!!
                    Logging.i(
                        tag,
                        "User $userName [$userIP] pressed button DELETE for worker ${htmlBirthdayParam.workerParam.workerName} - ${htmlBirthdayParam.workerParam.workerId}"
                    )
                    birthdayList.remove(htmlBirthdayParam.workerParam.workerId)
                    BirthdayRepository().delete(htmlBirthdayParam.workerParam.workerId)
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
                    birthdayList[htmlBirthdayParam.workerParam.workerId] = htmlBirthdayParam
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










