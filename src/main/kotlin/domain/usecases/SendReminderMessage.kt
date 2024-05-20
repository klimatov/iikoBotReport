package domain.usecases

import domain.models.ReminderParam
import domain.repository.BotRepository

class SendReminderMessage(private val botRepository: BotRepository) {
    private val tag = this::class.java.simpleName

    suspend fun execute(reminderParam: ReminderParam, preliminary: Boolean = false): Boolean {
        val messageText = FormatText().reminder(reminderParam, preliminary)
        if ((messageText.isNotEmpty()) && (reminderParam.sendChatId.isNotEmpty())) {
            var resultFlag = true
            reminderParam.sendChatId.forEach {
                if (!botRepository.sendMessageToChat(text = messageText, sendChatId = it)) {
                    resultFlag = false
                }
            }
            return resultFlag // TODO: переделать на отправку результата по каждому адресату
        } else return false
    }
}