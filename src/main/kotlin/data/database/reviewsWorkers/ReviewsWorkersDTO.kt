package data.database.reviewsWorkers

import kotlinx.serialization.Serializable
import models.ReviewsWorkerParam
import models.WorkerParam

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
    val sendDateTimeList: List<String>,
    val sendIfRating: List<Int>,
)

fun ReviewsWorkerParam.mapToReviewsWorkersDTO(): ReviewsWorkersDTO = ReviewsWorkersDTO(
    reviewsText = reviewsText,
    sendIfRating = sendIfRating,
    workerId = workerParam.workerId,
    workerName = workerParam.workerName,
    sendChatId = workerParam.sendChatId,
    sendWhenType = workerParam.sendWhenType,
    sendPeriod = workerParam.sendPeriod,
    sendTime = workerParam.sendTime,
    sendWeekDay = workerParam.sendWeekDay,
    sendMonthDay = workerParam.sendMonthDay,
    nameInHeader = workerParam.nameInHeader,
    workerIsActive = workerParam.workerIsActive,
    sendDateTimeList = workerParam.sendDateTimeList,
)

fun ReviewsWorkersDTO.mapToReviewsWorkerParam(): ReviewsWorkerParam = ReviewsWorkerParam(
    reviewsText = reviewsText,
    sendIfRating = sendIfRating,
    workerParam = WorkerParam(
        workerId = workerId,
        workerName = workerName,
        sendChatId = sendChatId,
        sendWhenType = sendWhenType,
        sendPeriod = sendPeriod,
        sendTime = sendTime,
        sendWeekDay = sendWeekDay,
        sendMonthDay = sendMonthDay,
        nameInHeader = nameInHeader,
        workerIsActive = workerIsActive,
        sendDateTimeList = sendDateTimeList
    )
)