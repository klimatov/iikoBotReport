package webServer

import core.WorkersManager
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import utils.Logging

@Serializable
data class Response(
    val result: String,
    val message: String,
)

fun Application.configureSendNow(workersManager: WorkersManager) {
    val tag = "configureSendNow"
    routing {
        authenticate("auth-basic") {
            get("/send-now") {
                val workerId = call.parameters["workerId"] ?: ""
                Logging.d(tag, "Json: $workerId")

                val response = if (workersManager.sendNowWorkerMessage(workerId))
                    Response("OK", "Отчет/напоминание отправлен")
                else
                    Response("FALSE", "Отправить отчет/напоминание не удалось")

                call.respond(response)
            }
        }
    }
}

