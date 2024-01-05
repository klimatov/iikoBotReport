import SecurityData.WEB_HOST
import SecurityData.WEB_PORT
import core.Bot
import core.WorkersManager
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.cachingheaders.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import webServer.*

val job = SupervisorJob()
private val bot by lazy { Bot(job) }
private val workersManager by lazy(LazyThreadSafetyMode.NONE) { WorkersManager(bot) }

fun main() {
    CoroutineScope(Dispatchers.Default + job).launch {
        bot.start()
        workersManager.start()
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
        configureEditWorker(workersManager)
        configureEditReminder(workersManager)
        configureEditBirthday(workersManager)
        configureEditNameIdBundle()
    }.start(wait = true)

    while (true) {
    }


}

