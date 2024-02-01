package webServer

import core.WorkersManager
import data.NameIdBundleRepository
import data.TwoGisRepository
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
import models.TwoGisWorkerParam
import models.WorkerParam
import models.WorkerState
import utils.Logging
import java.io.File
import java.util.*

fun Application.configureEditTwoGis(workersManager: WorkersManager) {
    val tag = "configureEditTwoGis"
    routing {
        authenticate("auth-basic") {

            static("/") {
                staticRootFolder = File("")
                files("css")
            }

            get("/edit-twogis") {
                val nameIdBundleList = NameIdBundleRepository.get()
                val twoGisReviewsList = TwoGisRepository().get()
                val newWorkerId = UUID.randomUUID().toString()
                var editTwoGisParam = TwoGisWorkerParam(
                    workerParam = WorkerParam(
                        workerId = newWorkerId, // ok
                        workerName = "Отчет об отзывах 2GIS -${newWorkerId.take(8)}", // ok
                        sendChatId = listOf(), // ok
                        sendPeriod = 15, // частота запроса к серверу отзывов (в минутах)
                        sendWhenType = 1, //1 - периодически
                        nameInHeader = false
                    ),
                    twoGisText = "\uD83D\uDCAC[TEXT]\n" +
                            "\n" +
                            "\uD83D\uDDD2\uFE0FНовый отзыв на портале [PROVIDER] с оценкой [RATING]⭐ о баре \uD83C\uDF78\"[OUTLET]\" оставил [USERNAME] в [DATECREATED].\n" +
                            "\n" +
                            "Пользователь [USERNAME] (ID: [USERID]) ранее оставил [USERREVIEWSCOUNT] отзывов.\n" +
                            "\n" +
                            "\n" +
                            "[OUTLET] - Бар на который оставлен отзыв\n" +
                            "[OBJECTID] – ID бара в системе 2GIS\n" +
                            "[ID] – ID отзыва\n" +
                            "[TEXT] - Текст отзыва\n" +
                            "[RATING] - Оценка\n" +
                            "[DATECREATED] - Дата создания отзыва\n" +
                            "[DATEEDITED] – Дата редактирования отзыва\n" +
                            "[PROVIDER] - Источник\n" +
                            "[ISHIDDEN] – Скрыт ли отзыв\n" +
                            "[LIKESCOUNT] – Количество лайков\n" +
                            "[COMMENTSCOUNT] – Количество комментариев\n" +
                            "[ONMODERATION] – На модерации?\n" +
                            "[USERID] – ID пользователя\n" +
                            "[USERREVIEWSCOUNT] – Количество отзывов пользователя\n" +
                            "[USERFIRSTNAME] – Имя пользователя\n" +
                            "[USERLASTNAME] – Фамилия пользователя\n" +
                            "[USERNAME] – Полное имя пользователя\n" +
                            "[USERPUBLICID] – Публичный ID пользователя", // текст отчета об отзыве
                )
                val workerId = call.request.queryParameters["workerId"]
                if (twoGisReviewsList.containsKey(workerId) == true) editTwoGisParam = twoGisReviewsList[workerId]!!

                call.respondHtml(HttpStatusCode.OK) {
                    head {
                        title {
                            +"iikoBotReport edit 2GIS reviews reporter"
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

                            workerIdField(editTwoGisParam.workerParam.workerId, editTwoGisParam.workerParam.workerIsActive, workerTypeName = "Отчет об отзывах 2GIS")

                            workerNameField(editTwoGisParam.workerParam.workerName, editTwoGisParam.workerParam.nameInHeader, workerTypeName = "отчета об отзывах 2GIS")

                            sendChatIdField(editTwoGisParam.workerParam.sendChatId, nameIdBundleList)

                            updateFrequency(editTwoGisParam.workerParam.sendPeriod)

//!!!!! ---------------------------------------------------------------------------------------------------------------
                            p(classes = "field required") {
                                label(classes = "label") {
                                    br()
                                    +"Текст сообщения о новом отзыве на портале 2GIS"
                                }
                                textArea(classes = "textarea") {
                                    name = "twoGisText"
                                    rows = "20"
                                    cols = "80"
                                    required = true
                                    +editTwoGisParam.twoGisText
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

            post("/edit-twogis") {
                val twoGisReviewsList = TwoGisRepository().get()
                val receiveParam: Map<String, List<String>> = call.receiveParameters().toMap()
                Logging.d(tag, receiveParam.toString())
                val userIP = call.request.origin.remoteHost
                val userName = call.principal<UserIdPrincipal>()?.name
                val htmlReviewsParam = TwoGisWorkerParam(
                    workerParam = WorkerParam(workerId = receiveParam["workerId"]?.joinToString() ?: "", // ok
                        workerName = receiveParam["workerName"]?.joinToString() ?: "",
                        sendChatId = receiveParam["sendChatId"]?.map { it.toLong() } ?: listOf(),
                        sendWhenType = 1,
                        sendPeriod = receiveParam["updateFrequency"]?.joinToString()?.toInt() ?: 5,
                        sendTime = listOf(receiveParam["sendTime"]?.joinToString() ?: ""),
                        sendWeekDay = listOf(1),
                        sendMonthDay = listOf(1),
                        nameInHeader = receiveParam["nameInHeader"]?.joinToString().toString() == "on",
                        workerIsActive = receiveParam["workerIsActive"]?.joinToString().toString() == "on",
                        sendDateTimeList = (listOf()),
                    ),
                    twoGisText = receiveParam["twoGisText"]?.joinToString() ?: "",
                )

                if (receiveParam.containsKey("deleteButton")) {                                 // - DELETE !!!
                    Logging.i(
                        tag,
                        "User $userName [$userIP] pressed button DELETE for worker ${htmlReviewsParam.workerParam.workerName} - ${htmlReviewsParam.workerParam.workerId}"
                    )
                    twoGisReviewsList.remove(htmlReviewsParam.workerParam.workerId)
                    TwoGisRepository().delete(htmlReviewsParam.workerParam.workerId)
                    workersManager.makeChangeWorker(
                        workerState = WorkerState.DELETE,
                        workerData = htmlReviewsParam
                    )
                }

                if (receiveParam.containsKey("saveButton")) {                                   // - SAVE !!!
                    Logging.i(
                        tag,
                        "User $userName [$userIP] pressed button SAVE for worker ${htmlReviewsParam.workerParam.workerName} - ${htmlReviewsParam.workerParam.workerId}"
                    )
                    twoGisReviewsList.put(htmlReviewsParam.workerParam.workerId, htmlReviewsParam)
                    TwoGisRepository().set(twoGisReviewsList)
                    workersManager.makeChangeWorker(
                        workerState = WorkerState.UPDATE,
                        workerData = htmlReviewsParam
                    )
                }
                call.respondRedirect("/")
            }
        }
    }
}