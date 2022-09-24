import SecurityData.WEB_HOST
import SecurityData.WEB_PORT
import core.Bot
import core.ReportManager
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import webServer.configureAuth
import webServer.configureEditWorker
import webServer.configureRouting
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.cachingheaders.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import webServer.configureEditReminder

val job = SupervisorJob()
private val bot by lazy { Bot(job) }
private val reportManager by lazy(LazyThreadSafetyMode.NONE) { ReportManager(bot) }

fun main() {
    CoroutineScope(Dispatchers.Default + job).launch {
        bot.start()
        reportManager.start()
    }.start()

    embeddedServer(factory = Netty, port = WEB_PORT, host = WEB_HOST) {
        install(CachingHeaders) {
            options { _, outgoingContent ->
                if (outgoingContent.contentType?.withoutParameters()?.match(ContentType.Image.Any) == true) {
                    CachingOptions(
                        cacheControl = CacheControl.MaxAge(
                            mustRevalidate = false,
                            maxAgeSeconds = 6 * 30 * 24 * 60 * 60,
                            visibility = CacheControl.Visibility.Public
                        )
                    )
                } else {
                    null
                }
            }
        }
        configureAuth()
        configureRouting()
        configureEditWorker(reportManager)
        configureEditReminder(reportManager)
    }.start(wait = true)

    while (true) {
    }


}

