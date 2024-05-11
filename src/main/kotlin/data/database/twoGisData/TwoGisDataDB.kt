package data.database.twoGisData

import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.json.json
import org.jetbrains.exposed.sql.transactions.transaction
import utils.Logging

object TwoGisDataDB : Table("twogis_data") {
    private val tag = this::class.java.simpleName
    private val workerId = TwoGisDataDB.varchar("worker_id", 40)
    private val twoGisShownReviews = TwoGisDataDB.json<List<TwoGisShownReviewsDTO>>("shown_reviews", Json.Default)
    override val primaryKey = PrimaryKey(workerId)

    fun insert(twoGisDataDTO: TwoGisDataDTO): Boolean {
        try {
            transaction {
                addLogger(StdOutSqlLogger)
                TwoGisDataDB.upsert {
                    it[workerId] = twoGisDataDTO.workerId
                    it[twoGisShownReviews] = twoGisDataDTO.twoGisShownReviews
                }
            }
            return true
        } catch (e: Exception) {
            Logging.e(tag, e.toString())
            return false
        }
    }

    fun getAll(): List<TwoGisDataDTO> {
        return try {
            transaction {
                addLogger(StdOutSqlLogger)
                TwoGisDataDB.selectAll().toList().map {
                    TwoGisDataDTO(
                        workerId = it[workerId],
                        twoGisShownReviews = it[twoGisShownReviews],
                    )
                }
            }
        } catch (e: Exception) {
            Logging.e(tag, e.toString())
            listOf()
        }
    }

    fun getByWorkerId(workerId: String): TwoGisDataDTO? {
        return try {
            transaction {
                addLogger(StdOutSqlLogger)
                val result = TwoGisDataDB.selectAll().where { TwoGisDataDB.workerId.eq(workerId) }.single()
                TwoGisDataDTO(
                    workerId = workerId,
                    twoGisShownReviews = result[twoGisShownReviews]
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
                TwoGisDataDB.deleteWhere {
                    TwoGisDataDB.workerId.eq(workerId)
                }
            }
            return true
        } catch (e: Exception) {
            Logging.e(tag, e.toString())
            return false
        }
    }

}
