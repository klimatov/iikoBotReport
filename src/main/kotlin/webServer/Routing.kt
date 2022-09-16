package webServer.plugins

import data.fileProcessing.NotesRepository
import data.fileProcessing.WorkersRepository
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.*
import kotlinx.html.*
import utils.Logging

fun Application.configureRouting() {
    val tag = "configureRouting"
    routing {
        authenticate("auth-basic") {
            get("/") {
                call.respondHtml(HttpStatusCode.OK) {
                    head {
                        title {
                            +"iikoBotReport list workers"
                        }
                    }
                    body {
                        val workerList = WorkersRepository().get()
                        val notes = NotesRepository().get()

                        +"Список активных отчетов:"
                        br()
                        workerList?.keys?.forEach {
                            a(href = "/edit-worker?workerId=$it") {
                                title = it
                                +"${workerList[it]?.workerName}"
                            }
                            br()

                        }
                        a(href = "edit-worker") {
                            +"Создать новый отчет"
                        }

                        repeat(3) { br() }

                        postForm {
                            + "Заметки:"
                            br()
                            textArea{
                                name = "notes"
                                rows = "20"
                                cols = "80"
                                +notes
                            }

                            br()
                            button(type = ButtonType.submit) {
                                name = "saveButton"
                                +"СОХРАНИТЬ"
                            }
                        }
                    }
                }
            }

            post("/") {
                val receiveParam: Map<String, List<String>> = call.receiveParameters().toMap()
                Logging.d(tag,receiveParam.toString())
                val notes = receiveParam["notes"]?.joinToString()

                if (receiveParam.containsKey("saveButton")) {                                   // - SAVE !!!
                    Logging.i(tag,"Нажата кнопка SAVE")
                    NotesRepository().set(notes)
                }
                call.respondRedirect("/")
            }
        }
    }
}
