package domain.repository

interface BotRepository {
    suspend fun sendMessageToChat(text: String, sendChatId: Long, photosList: List<String> = listOf()): Boolean
}