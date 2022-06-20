package domain.usecases

import domain.models.MessageParam
import domain.models.ReportResult
import org.junit.jupiter.api.Test

internal class FormatTextTest {

    @Test
    fun reportTest() {
        val table: MutableList<MutableList<String>> = mutableListOf(
            mutableListOf("Концепция", "Сумма со скидкой"),
            mutableListOf("Мира86", "139099.00"),
            mutableListOf("Остров домики", "2645.00"),
            mutableListOf("Ян23", "16946.30"),
            mutableListOf("Ян25", "53728.20")
        )

        val tableOld: MutableList<MutableList<String>> = mutableListOf(
            mutableListOf("Концепция", "Сумма со скидкой"),
            mutableListOf("Мира86", "139099.00"),
            mutableListOf("Остров домики", "2646.00"),
            mutableListOf("Ян23", "16946.30"),
            mutableListOf("Ян25", "53728.20")
        )
        val messageParam: MessageParam = MessageParam(
            reportResult = ReportResult(table),
            oldReport = ReportResult(tableOld),
            sendChatId = 0,
            messageHeader = false,
            messageSuffix = mapOf(Pair(1," руб."),Pair(10," шт.")),
            messageAmount = 2
        )


        val text = FormatText().report(messageParam)

        println(text)

    }
}