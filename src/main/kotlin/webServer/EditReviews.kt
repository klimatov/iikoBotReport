package webServer

import core.WorkersManager
import data.ReviewsRepository
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
import models.ReviewsWorkerParam
import models.WorkerParam
import models.WorkerState
import utils.Logging
import java.io.File
import java.util.*

fun Application.configureEditReviews(workersManager: WorkersManager) {
    val tag = "configureEditReviews"
    routing {
        authenticate("auth-basic") {

            static("/") {
                staticRootFolder = File("")
                files("css")
            }

            get("/edit-reviews") {
                val nameIdBundleList = NameIdBundleRepository.get()
                val reviewsList = ReviewsRepository().get()
                val newWorkerId = UUID.randomUUID().toString()
                var editReviewsParam = ReviewsWorkerParam(
                    workerParam = WorkerParam(
                        workerId = newWorkerId, // ok
                        workerName = "Отчет об отзывах -${newWorkerId.take(8)}", // ok
                        sendChatId = listOf(), // ok
                        sendPeriod = 5, // частота запроса к серверу отзывов (в минутах)
                        sendWhenType = 1, //1 - периодически
                        nameInHeader = false
                    ),
                    reviewsText = "", // текст отчета
                )
                val workerId = call.request.queryParameters["workerId"]
                if (reviewsList.containsKey(workerId) == true) editReviewsParam = reviewsList[workerId]!!

                call.respondHtml(HttpStatusCode.OK) {
                    head {
                        title {
                            +"iikoBotReport edit reviews reporter"
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

                            workerIdField(editReviewsParam.workerParam.workerId, editReviewsParam.workerParam.workerIsActive, workerTypeName = "Отчет об отзывах")

                            workerNameField(editReviewsParam.workerParam.workerName, editReviewsParam.workerParam.nameInHeader, workerTypeName = "отчета об отзывах")

                            sendChatIdField(editReviewsParam.workerParam.sendChatId, nameIdBundleList)

                            updateFrequency(editReviewsParam.workerParam.sendPeriod)

//!!!!! ---------------------------------------------------------------------------------------------------------------
                            p(classes = "field required") {
                                label(classes = "label") {
                                    br()
                                    +"Текст сообщения о новом отзыве"
                                }
                                textArea(classes = "textarea") {
                                    name = "reviewsText"
                                    rows = "20"
                                    cols = "80"
                                    required = true
                                    +editReviewsParam.reviewsText
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

            post("/edit-reviews") {
                val reviewsList = ReviewsRepository().get()
                val receiveParam: Map<String, List<String>> = call.receiveParameters().toMap()
                Logging.d(tag, receiveParam.toString())
                val userIP = call.request.origin.remoteHost
                val userName = call.principal<UserIdPrincipal>()?.name
                val htmlReviewsParam = ReviewsWorkerParam(
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
                    reviewsText = receiveParam["reviewsText"]?.joinToString() ?: "",
                )

                if (receiveParam.containsKey("deleteButton")) {                                 // - DELETE !!!
                    Logging.i(
                        tag,
                        "User $userName [$userIP] pressed button DELETE for worker ${htmlReviewsParam.workerParam.workerName} - ${htmlReviewsParam.workerParam.workerId}"
                    )
                    reviewsList.remove(htmlReviewsParam.workerParam.workerId)
                    ReviewsRepository().delete(htmlReviewsParam.workerParam.workerId)
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
                    reviewsList.put(htmlReviewsParam.workerParam.workerId, htmlReviewsParam)
                    ReviewsRepository().set(reviewsList)
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