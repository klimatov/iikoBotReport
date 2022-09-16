import SecurityData.WEB_HOST
import SecurityData.WEB_PORT
import core.Bot
import core.ReportManager
import webServer.plugins.configureAuth
import webServer.plugins.configureEditWorker
import webServer.plugins.configureRouting
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

val job = SupervisorJob()
private val bot by lazy { Bot(job) }
private val reportManager by lazy(LazyThreadSafetyMode.NONE) { ReportManager(bot) }

fun main() {
    CoroutineScope(Dispatchers.Default + job).launch {
        bot.start()
        reportManager.start()
    }.start()

    embeddedServer(factory = Netty, port = WEB_PORT, host = WEB_HOST) {
        configureAuth()
        configureRouting()
        configureEditWorker(reportManager)
    }.start(wait = true)

    while (true) {
    }


}

