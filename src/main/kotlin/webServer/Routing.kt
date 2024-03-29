package webServer

import MyTest
import data.*
import data.fileProcessing.NotesRepository
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
import utils.Logging
import java.io.File

fun Application.configureRouting() {
    val tag = "configureRouting"
    routing {
        authenticate("auth-basic") {

            static("/") {
                staticRootFolder = File("")
                files("css")
            }

            get("/") {
                call.respondHtml(HttpStatusCode.OK) {
                    head {
                        title {
                            +"iikoBotReport list workers"
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
                        val reportsList = ReportsRepository().get()
                        val remindersList = RemindersRepository().get()
                        val birthdayList = BirthdayRepository().get()
                        val reviewsList = ReviewsRepository().get()
                        val twoGisReviewsList = TwoGisRepository().get()
                        val notes = NotesRepository().get()
                        postForm(classes = "form") {

                            label(classes = "label list collapsed") {
                                onClick = "hideBlock(this)"
                                +"Список отчетов (${reportsList.count()}):"
                            }
                            span(classes = "field list") {
                                style = "display: none;"
                                reportsList.keys.forEach {
                                    p(classes = "field") {
                                        a(href = "/edit-worker?workerId=$it", classes = "text-input") {
                                            style = "text-decoration: none;"
                                            title = it
                                            +"${reportsList[it]?.workerParam?.workerName}"
                                            if (reportsList[it]?.workerParam?.workerIsActive != true) {
                                                span {
                                                    style = "color:red;"
                                                    +" (не активен)"
                                                }
                                            }
                                            img(classes = "refresh", alt = "SEND NOW", src = "send.png") {
                                                onClick = "sendNow(this)"
                                            }
                                        }
                                    }
                                }
                            }
                            p(classes = "field") {
                                a(href = "edit-worker", classes = "text-input") {
                                    style = "text-decoration: none;"
                                    +"Создать новый отчет"
                                }
                            }

                            label(classes = "label list collapsed") {
                                onClick = "hideBlock(this)"
                                +"Список напоминаний (${remindersList.count()}):"
                            }
                            span(classes = "field list") {
                                style = "display: none;"
                                remindersList.keys.forEach {
                                    p(classes = "field") {
                                        a(href = "/edit-reminder?workerId=$it", classes = "text-input") {
                                            style = "text-decoration: none;"
                                            title = it
                                            +"${remindersList[it]?.workerParam?.workerName}"
                                            if (remindersList[it]?.workerParam?.workerIsActive != true) {
                                                span {
                                                    style = "color:red;"
                                                    +" (не активен)"
                                                }
                                            }
                                            img(classes = "refresh", alt = "SEND NOW", src = "send.png") {
                                                onClick = "sendNow(this)"
                                            }
                                        }
                                    }
                                }
                            }
                            p(classes = "field") {
                                a(href = "edit-reminder", classes = "text-input") {
                                    style = "text-decoration: none;"
                                    +"Создать новое напоминание"
                                }
                            }

                            label(classes = "label list collapsed") {
                                onClick = "hideBlock(this)"
                                +"Список напоминаний о ДР (${birthdayList.count()}):"
                            }
                            span(classes = "field list") {
                                style = "display: none;"
                                birthdayList.keys.forEach {
                                    p(classes = "field") {
                                        a(href = "/edit-birthday?workerId=$it", classes = "text-input") {
                                            style = "text-decoration: none;"
                                            title = it
                                            +"${birthdayList[it]?.workerParam?.workerName}"
                                            if (birthdayList[it]?.workerParam?.workerIsActive != true) {
                                                span {
                                                    style = "color:red;"
                                                    +" (не активен)"
                                                }
                                            }
                                            img(classes = "refresh", alt = "SEND NOW", src = "send.png") {
                                                onClick = "sendNow(this)"
                                            }
                                        }
                                    }
                                }
                            }
                            p(classes = "field") {
                                a(href = "edit-birthday", classes = "text-input") {
                                    style = "text-decoration: none;"
                                    +"Создать новое напоминание о ДР"
                                }
                            }

                            label(classes = "label list collapsed") {
                                onClick = "hideBlock(this)"
                                +"Список отчетов об отзывах из приложения (${reviewsList.count()}):"
                            }
                            span(classes = "field list") {
                                style = "display: none;"
                                reviewsList.keys.forEach {
                                    p(classes = "field") {
                                        a(href = "/edit-reviews?workerId=$it", classes = "text-input") {
                                            style = "text-decoration: none;"
                                            title = it
                                            +"${reviewsList[it]?.workerParam?.workerName}"
                                            if (reviewsList[it]?.workerParam?.workerIsActive != true) {
                                                span {
                                                    style = "color:red;"
                                                    +" (не активен)"
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            p(classes = "field") {
                                a(href = "edit-reviews", classes = "text-input") {
                                    style = "text-decoration: none;"
                                    +"Создать новый отчет об отзывах из приложения"
                                }
                            }

                            label(classes = "label list collapsed") {
                                onClick = "hideBlock(this)"
                                +"Список отчетов об отзывах из 2GIS (${twoGisReviewsList.count()}):"
                            }
                            span(classes = "field list") {
                                style = "display: none;"
                                twoGisReviewsList.keys.forEach {
                                    p(classes = "field") {
                                        a(href = "/edit-twogis?workerId=$it", classes = "text-input") {
                                            style = "text-decoration: none;"
                                            title = it
                                            +"${twoGisReviewsList[it]?.workerParam?.workerName}"
                                            if (twoGisReviewsList[it]?.workerParam?.workerIsActive != true) {
                                                span {
                                                    style = "color:red;"
                                                    +" (не активен)"
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            p(classes = "field") {
                                a(href = "edit-twogis", classes = "text-input") {
                                    style = "text-decoration: none;"
                                    +"Создать новый отчет об отзывах из 2GIS"
                                }
                            }


                            label(classes = "label") {
                                +"Настройки:"
                            }
                            p(classes = "field") {
                                a(href = "edit-name-id-bundle", classes = "text-input") {
                                    style = "text-decoration: none;"
                                    +"Настроить список адресатов telegram"
                                }
                            }

                            p(classes = "field") {
                                label(classes = "label") {
                                    br()
                                    +"Заметки:"
                                }
                                textArea(classes = "textarea") {
                                    name = "notes"
                                    rows = "20"
                                    cols = "80"
                                    +notes
                                }
                            }
                            p(classes = "field") {
                                input(type = InputType.submit, classes = "button") {
                                    name = "saveButton"
                                    value = "Сохранить"
                                }
                            }

//                            p(classes = "field") {
//                                input(type = InputType.submit, classes = "button") {
//                                    name = "testButton"
//                                    value = "Тест"
//                                }
//                            }


                        }
                        script(type = "text/javascript", src = "js/main.js") {}
                    }
                }
            }

            post("/") {
                val receiveParam: Map<String, List<String>> = call.receiveParameters().toMap()
                Logging.d(tag, receiveParam.toString())
                val userIP = call.request.origin.remoteHost
                val userName = call.principal<UserIdPrincipal>()?.name
                val notes = receiveParam["notes"]?.joinToString()

                if (receiveParam.containsKey("saveButton")) {                                   // - SAVE !!!
                    Logging.i(
                        tag,
                        "User $userName [$userIP] pressed button SAVE for notes"
                    )
                    NotesRepository().set(notes)
                }
                if (receiveParam.containsKey("testButton")) {                                   // - SAVE !!!
                    Logging.i(
                        tag,
                        "User $userName [$userIP] pressed button TEST !!!"
                    )
                    MyTest().test()
                }

                call.respondRedirect("/")
            }
        }
    }
}
