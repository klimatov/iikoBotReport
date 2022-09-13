package httpServer.plugins

import data.fileProcessing.WorkersRepository
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.html.*
import kotlinx.html.*

fun Application.configureRouting() {

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
                    }
                }
            }
        }
    }
}
