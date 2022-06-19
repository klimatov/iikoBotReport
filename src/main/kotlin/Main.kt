import core.Bot
import core.ReportManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

val job = SupervisorJob()
private val bot by lazy { Bot(job) }
private val reportManager by lazy(LazyThreadSafetyMode.NONE) { ReportManager(bot) }

fun main(args: Array<String>) {
    CoroutineScope(Dispatchers.Default + job).launch {
        bot.start()
        reportManager.start()
    }.start()

    while (true) {
    }
}

