package data.database.reviewsData

import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.json.json
import org.jetbrains.exposed.sql.transactions.transaction
import utils.Logging

object ReviewsDataDB : Table("reviews_data") {
    private val tag = this::class.java.simpleName
    private val workerId = ReviewsDataDB.varchar("worker_id", 40)
    private val shownReviews = ReviewsDataDB.json<List<ShownReviewsDTO>>("shown_reviews", Json.Default)
    override val primaryKey = PrimaryKey(workerId)

    fun insert(reviewsDataDTO: ReviewsDataDTO): Boolean {
        try {
            transaction {
                addLogger(StdOutSqlLogger)
                ReviewsDataDB.upsert {
                    it[workerId] = reviewsDataDTO.workerId
                    it[shownReviews] = reviewsDataDTO.shownReviews
                }
            }
            return true
        } catch (e: Exception) {
            Logging.e(tag, e.toString())
            return false
        }
    }

    fun getAll(): List<ReviewsDataDTO> {
        return try {
            transaction {
                addLogger(StdOutSqlLogger)
                ReviewsDataDB.selectAll().toList().map {
                    ReviewsDataDTO(
                        workerId = it[workerId],
                        shownReviews = it[shownReviews]
                    )
                }
            }
        } catch (e: Exception) {
            Logging.e(tag, e.toString())
            listOf()
        }
    }

    fun getByWorkerId(workerId: String): ReviewsDataDTO? {
        return try {
            transaction {
                addLogger(StdOutSqlLogger)
                val result = ReviewsDataDB.selectAll().where { ReviewsDataDB.workerId.eq(workerId) }.single()
                ReviewsDataDTO(
                    workerId = workerId,
                    shownReviews = result[shownReviews]
                )
            }
        } catch (e: Exception) {
            Logging.e(tag, e.toString())
            null
        }
    }

    fun deleteByWorkerId(workerId: String): Boolean {
        try {
            transaction {
                addLogger(StdOutSqlLogger)
                ReviewsDataDB.deleteWhere {
                    ReviewsDataDB.workerId.eq(workerId)
                }
            }
            return true
        } catch (e: Exception) {
            Logging.e(tag, e.toString())
            return false
        }
    }

}
