package domain.repository

interface BotRepository {
    suspend fun sendMessageToChat(text: String): Boolean
}