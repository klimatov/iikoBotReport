package models

data class BundleParam(
    val botUserId: Int,
    val name: String,
    val telegramId: Long,
    val available: IdAvailability
)
enum class IdAvailability {
    YES,
    NO,
    NOT_CHECKED
}
