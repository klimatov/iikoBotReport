package data.database.reviewsWorkers

import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.json.json
import org.jetbrains.exposed.sql.transactions.transaction
import utils.Logging

object ReviewsWorkersDB : Table("reviews_workers") {
    private val tag = this::class.java.simpleName

    private val reviewsText = ReviewsWorkersDB.text("reviews_text")
    private val workerId = ReviewsWorkersDB.varchar("worker_id", 40)
    private val workerName = ReviewsWorkersDB.varchar("worker_name", 255)
    private val sendChatId = ReviewsWorkersDB.json<LongArray>("send_chat_id", Json.Default)
    private val sendWhenType = ReviewsWorkersDB.integer("send_when_type")
    private val sendPeriod = ReviewsWorkersDB.integer("send_period")
    private val sendTime = ReviewsWorkersDB.json<Array<String>>("send_time", Json.Default)
    private val sendWeekDay = ReviewsWorkersDB.json<IntArray>("send_week_day", Json.Default)
    private val sendMonthDay = ReviewsWorkersDB.json<IntArray>("send_month_day", Json.Default)
    private val nameInHeader = ReviewsWorkersDB.bool("name_in_header")
    private val workerIsActive = ReviewsWorkersDB.bool("worker_is_active")
    private val sendDateTimeList = ReviewsWorkersDB.json<Array<String>>("send_date_time_list", Json.Default)
    override val primaryKey = PrimaryKey(workerId)

    fun createTable(createdTable: Table): Boolean {
        try {
            transaction {
                addLogger(StdOutSqlLogger)
                SchemaUtils.createMissingTablesAndColumns(createdTable)
            }
            return true
        } catch (e: Exception) {
            Logging.e(tag, e.toString())
            return false
        }
    }

    fun listTables(): List<String> {
        try {
            var listTables: List<String> = listOf()
            transaction {
                addLogger(StdOutSqlLogger)
                listTables = SchemaUtils.listTables()
            }
            return listTables

        } catch (e: Exception) {
            Logging.e(tag, e.toString())
            return listOf()
        }
    }

    fun insert(reviewsWorkersDTO: ReviewsWorkersDTO): Boolean {
        try {
            transaction {
                addLogger(StdOutSqlLogger)
                ReviewsWorkersDB.upsert {
                    it[reviewsText] = reviewsWorkersDTO.reviewsText
                    it[workerId] = reviewsWorkersDTO.workerId
                    it[workerName] = reviewsWorkersDTO.workerName
                    it[sendChatId] = reviewsWorkersDTO.sendChatId.toLongArray()
                    it[sendWhenType] = reviewsWorkersDTO.sendWhenType
                    it[sendPeriod] = reviewsWorkersDTO.sendPeriod
                    it[sendTime] = reviewsWorkersDTO.sendTime.toTypedArray()
                    it[sendWeekDay] = reviewsWorkersDTO.sendWeekDay.toIntArray()
                    it[sendMonthDay] = reviewsWorkersDTO.sendMonthDay.toIntArray()
                    it[nameInHeader] = reviewsWorkersDTO.nameInHeader
                    it[workerIsActive] = reviewsWorkersDTO.workerIsActive
                    it[sendDateTimeList] = reviewsWorkersDTO.sendDateTimeList.toTypedArray()
                }

            }
            return true
        } catch (e: Exception) {
            Logging.e(tag, e.toString())
            return false
        }
    }

    fun update(reviewsWorkersDTO: ReviewsWorkersDTO): Boolean {
        try {
            transaction {
                addLogger(StdOutSqlLogger)
                ReviewsWorkersDB.update({ workerId eq reviewsWorkersDTO.workerId }) {
                    it[reviewsText] = reviewsWorkersDTO.reviewsText
//                    it[workerId] = reviewsWorkersDTO.workerId
                    it[workerName] = reviewsWorkersDTO.workerName
                    it[sendChatId] = reviewsWorkersDTO.sendChatId.toLongArray()
                    it[sendWhenType] = reviewsWorkersDTO.sendWhenType
                    it[sendPeriod] = reviewsWorkersDTO.sendPeriod
                    it[sendTime] = reviewsWorkersDTO.sendTime.toTypedArray()
                    it[sendWeekDay] = reviewsWorkersDTO.sendWeekDay.toIntArray()
                    it[sendMonthDay] = reviewsWorkersDTO.sendMonthDay.toIntArray()
                    it[nameInHeader] = reviewsWorkersDTO.nameInHeader
                    it[workerIsActive] = reviewsWorkersDTO.workerIsActive
                    it[sendDateTimeList] = reviewsWorkersDTO.sendDateTimeList.toTypedArray()
                }

            }
            return true
        } catch (e: Exception) {
            Logging.e(tag, e.toString())
            return false
        }
    }

    fun getAll(): List<ReviewsWorkersDTO> {
        return try {
            transaction {
                addLogger(StdOutSqlLogger)
                ReviewsWorkersDB.selectAll().toList().map {
                    ReviewsWorkersDTO(
                        reviewsText = it[reviewsText],
                        workerId = it[workerId],
                        workerName = it[workerName],
                        sendChatId = it[sendChatId].toList(),
                        sendWhenType = it[sendWhenType],
                        sendPeriod = it[sendPeriod],
                        sendTime = it[sendTime].toList(),
                        sendWeekDay = it[sendWeekDay].toList(),
                        sendMonthDay = it[sendMonthDay].toList(),
                        nameInHeader = it[nameInHeader],
                        workerIsActive = it[workerIsActive],
                        sendDateTimeList = it[sendDateTimeList].toList()
                    )
                }
            }
        } catch (e: Exception) {
            Logging.e(tag, e.toString())
            listOf()
        }
    }

    fun getByWorkerId(workerId: String): ReviewsWorkersDTO? {
        return try {
            transaction {
                addLogger(StdOutSqlLogger)
                val result = ReviewsWorkersDB.select { ReviewsWorkersDB.workerId.eq(workerId) }.single()
                ReviewsWorkersDTO(
                    reviewsText = result[reviewsText],
                    workerId = workerId,
                    workerName = result[workerName],
                    sendChatId = result[sendChatId].toList(),
                    sendWhenType = result[sendWhenType],
                    sendPeriod = result[sendPeriod],
                    sendTime = result[sendTime].toList(),
                    sendWeekDay = result[sendWeekDay].toList(),
                    sendMonthDay = result[sendMonthDay].toList(),
                    nameInHeader = result[nameInHeader],
                    workerIsActive = result[workerIsActive],
                    sendDateTimeList = result[sendDateTimeList].toList()
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
                ReviewsWorkersDB.deleteWhere {
                    ReviewsWorkersDB.workerId.eq(workerId)
                }
            }
            return true
        } catch (e: Exception) {
            Logging.e(tag, e.toString())
            return false
        }
    }

}
