import core.ReportManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

val job = SupervisorJob()
private val reportManager by lazy(LazyThreadSafetyMode.NONE) { ReportManager() }

fun main() {
    CoroutineScope(Dispatchers.Default + job).launch {
        reportManager.start()

    }.start()

    while (true) {
    }
}

