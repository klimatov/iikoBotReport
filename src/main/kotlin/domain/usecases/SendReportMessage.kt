package domain.usecases

import domain.models.MessageParam
import domain.repository.BotRepository

class SendReportMessage(private val botRepository: BotRepository) {
    private val tag = this::class.java.simpleName
    suspend fun execute(messageParam: MessageParam): Boolean {
        val messageText = FormatText().report(messageParam)

        if ((messageText.isNotEmpty()) && (messageParam.sendChatId.isNotEmpty())) {
            var resultFlag = true
            messageParam.sendChatId.forEach {
                if (!botRepository.sendMessageToChat(text = messageText, sendChatId = it)) {
                    resultFlag = false
                }
            }
            return resultFlag // TODO: переделать на отправку результата по каждому адресату
        } else return false
    }
}