package domain.models

class BirthdayParam (
    val sendChatId: List<Long> = listOf(), // список ID чатов/юзеров куда будет отправляться отчет
    val workerName: String = "", // название поздравления
    val nameInHeader: Boolean = false, // Выводить название отчета в заголовке сообщения
    val birthdayText: String = "", // текст сообщения о дне рождения
    val sendBeforeDays: Long = 30, // за сколько дней до ДР начать оповещать
)