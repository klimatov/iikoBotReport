package data.database.reviewsWorkers

import kotlinx.serialization.Serializable

@Serializable
data class ReviewsWorkersDTO(
    val reviewsText: String,
    val workerId: String,
    val workerName: String,
    val sendChatId: List<Long>,
    val sendWhenType: Int,
    val sendPeriod: Int,
    val sendTime: List<String>,
    val sendWeekDay: List<Int>,
    val sendMonthDay: List<Int>,
    val nameInHeader: Boolean,
    val workerIsActive: Boolean,
    val sendDateTimeList: List<String>
)