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
    REVIEWS
}

enum class WorkerState{
    CREATE,
    UPDATE,
    DELETE,
    WORK,
    DELETED
}

