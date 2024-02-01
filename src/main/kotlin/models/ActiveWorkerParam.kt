package models

data class ActiveWorkerParam(
    val workerId: String,
    val workerType: WorkerType,
    var workerState: WorkerState,
    val workerIsActive: Boolean
)

enum class WorkerType{
    REPORT,
    REMINDER,
    BIRTHDAY,
    REVIEWS,
    TWOGIS
}

enum class WorkerState{
    CREATE,
    UPDATE,
    DELETE,
    WORK,
    DELETED
}

