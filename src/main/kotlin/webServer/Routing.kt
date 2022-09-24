package webServer

import data.fileProcessing.NotesRepository
import data.fileProcessing.RemindersRepository
import data.fileProcessing.ReportsRepository
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
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
                        val notes = NotesRepository().get()
                        postForm(classes = "form") {

                            label(classes = "label") {
                                +"Список активных отчетов:"
                            }
                            reportsList.keys.forEach {
                                p(classes = "field") {
                                    a(href = "/edit-worker?workerId=$it", classes = "text-input") {
                                        style = "text-decoration: none;"
                                        title = it
                                        +"${reportsList[it]?.workerName}"
                                        if (reportsList[it]?.workerIsActive != true) {
                                            span {
                                                style = "color:red;"
                                                +" (не активен)"
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


                            label(classes = "label") {
                                +"Список активных напоминаний:"
                            }
                            remindersList.keys.forEach {
                                p(classes = "field") {
                                    a(href = "/edit-reminder?workerId=$it", classes = "text-input") {
                                        style = "text-decoration: none;"
                                        title = it
                                        +"${remindersList[it]?.workerName}"
                                        if (remindersList[it]?.workerIsActive != true) {
                                            span {
                                                style = "color:red;"
                                                +" (не активен)"
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

                        }
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
                call.respondRedirect("/")
            }
        }
    }
}
