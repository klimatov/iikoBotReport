package domain.usecases

import domain.models.ReminderParam
import domain.repository.BotRepository
import utils.Logging

class SendReminderMessage(private val botRepository: BotRepository) {
    private val tag = this::class.java.simpleName

    suspend fun execute(reminderParam: ReminderParam): Boolean {
        val messageText = FormatText().reminder(reminderParam)
        if (messageText.isNotEmpty()) {
            return botRepository.sendMessageToChat(text = messageText, sendChatId = reminderParam.sendChatId)
        } else return false
    }
}