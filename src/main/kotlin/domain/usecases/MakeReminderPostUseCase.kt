package domain.usecases

import domain.models.ReminderParam
import domain.repository.BotRepository
import utils.Logging

class MakeReminderPostUseCase(private val botRepository: BotRepository) {
    private val tag = this::class.java.simpleName

    suspend fun execute(reminderParam: ReminderParam, preliminary: Boolean = false) {
        val sendResult = SendReminderMessage(botRepository = botRepository).execute(reminderParam, preliminary)
        Logging.i(
            tag,
            "Напоминание ${reminderParam.workerName} ${if (sendResult) "отправлено в чат" else "отправить в чат НЕ УДАЛОСЬ"}..."
        )
    }
}