package domain.usecases

import domain.models.BirthdayParam
import domain.models.EmployeeModel
import domain.repository.BotRepository

class SendBirthdayMessage(private val botRepository: BotRepository) {
    private val tag = this::class.java.simpleName

    suspend fun execute(birthdayParam: BirthdayParam, celebratingEmployeesList: List<EmployeeModel>): Boolean {
        val messageText = FormatText().birthday(birthdayParam)
        if ((messageText.isNotEmpty()) && (birthdayParam.sendChatId.isNotEmpty())) {
            var resultFlag = true
            birthdayParam.sendChatId.forEach {
                if (!botRepository.sendMessageToChat(text = messageText, sendChatId = it)) {
                    resultFlag = false
                }
            }
            return resultFlag // TODO: переделать на отправку результата по каждому адресату
        } else return false
    }
}