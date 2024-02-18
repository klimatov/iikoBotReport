package data.database.twoGisWorkers

import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.json.json
import org.jetbrains.exposed.sql.transactions.transaction
import utils.Logging

object TwoGisWorkersDB : Table("twogis_workers") {
    private val tag = this::class.java.simpleName

    private val twoGisText = TwoGisWorkersDB.text("twogis_text")
        private val sendIfRating = TwoGisWorkersDB.json<IntArray>("send_if_rating", Json.Default).nullable()
    private val workerId = TwoGisWorkersDB.varchar("worker_id", 40)
    private val workerName = TwoGisWorkersDB.varchar("worker_name", 255)
    private val sendChatId = TwoGisWorkersDB.json<LongArray>("send_chat_id", Json.Default)
    private val sendWhenType = TwoGisWorkersDB.integer("send_when_type")
    private val sendPeriod = TwoGisWorkersDB.integer("send_period")
    private val sendTime = TwoGisWorkersDB.json<Array<String>>("send_time", Json.Default)
    private val sendWeekDay = TwoGisWorkersDB.json<IntArray>("send_week_day", Json.Default)
    private val sendMonthDay = TwoGisWorkersDB.json<IntArray>("send_month_day", Json.Default)
    private val nameInHeader = TwoGisWorkersDB.bool("name_in_header")
    private val workerIsActive = TwoGisWorkersDB.bool("worker_is_active")
    private val sendDateTimeList = TwoGisWorkersDB.json<Array<String>>("send_date_time_list", Json.Default)
    override val primaryKey = PrimaryKey(workerId)

    fun insert(twoGisWorkersDTO: TwoGisWorkersDTO): Boolean {
        try {
            transaction {
                addLogger(StdOutSqlLogger)
                TwoGisWorkersDB.upsert {
                    it[twoGisText] = twoGisWorkersDTO.twoGisText
                    it[sendIfRating] = twoGisWorkersDTO.sendIfRating.toIntArray()
                    it[workerId] = twoGisWorkersDTO.workerId
                    it[workerName] = twoGisWorkersDTO.workerName
                    it[sendChatId] = twoGisWorkersDTO.sendChatId.toLongArray()
                    it[sendWhenType] = twoGisWorkersDTO.sendWhenType
                    it[sendPeriod] = twoGisWorkersDTO.sendPeriod
                    it[sendTime] = twoGisWorkersDTO.sendTime.toTypedArray()
                    it[sendWeekDay] = twoGisWorkersDTO.sendWeekDay.toIntArray()
                    it[sendMonthDay] = twoGisWorkersDTO.sendMonthDay.toIntArray()
                    it[nameInHeader] = twoGisWorkersDTO.nameInHeader
                    it[workerIsActive] = twoGisWorkersDTO.workerIsActive
                    it[sendDateTimeList] = twoGisWorkersDTO.sendDateTimeList.toTypedArray()
                }

            }
            return true
        } catch (e: Exception) {
            Logging.e(tag, e.toString())
            return false
        }
    }

    fun update(twoGisWorkersDTO: TwoGisWorkersDTO): Boolean {
        try {
            transaction {
                addLogger(StdOutSqlLogger)
                TwoGisWorkersDB.update({ workerId eq twoGisWorkersDTO.workerId }) {
                    it[twoGisText] = twoGisWorkersDTO.twoGisText
                    it[sendIfRating] = twoGisWorkersDTO.sendIfRating.toIntArray()
//                    it[workerId] = twoGisWorkersDTO.workerId
                    it[workerName] = twoGisWorkersDTO.workerName
                    it[sendChatId] = twoGisWorkersDTO.sendChatId.toLongArray()
                    it[sendWhenType] = twoGisWorkersDTO.sendWhenType
                    it[sendPeriod] = twoGisWorkersDTO.sendPeriod
                    it[sendTime] = twoGisWorkersDTO.sendTime.toTypedArray()
                    it[sendWeekDay] = twoGisWorkersDTO.sendWeekDay.toIntArray()
                    it[sendMonthDay] = twoGisWorkersDTO.sendMonthDay.toIntArray()
                    it[nameInHeader] = twoGisWorkersDTO.nameInHeader
                    it[workerIsActive] = twoGisWorkersDTO.workerIsActive
                    it[sendDateTimeList] = twoGisWorkersDTO.sendDateTimeList.toTypedArray()
                }

            }
            return true
        } catch (e: Exception) {
            Logging.e(tag, e.toString())
            return false
        }
    }

    fun getAll(): List<TwoGisWorkersDTO> {
        return try {
            transaction {
                addLogger(StdOutSqlLogger)
                TwoGisWorkersDB.selectAll().toList().map {
                    TwoGisWorkersDTO(
                        twoGisText = it[twoGisText],
                        sendIfRating = it[sendIfRating]?.toList()?: listOf(1,2,3,4,5),
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

    fun getByWorkerId(workerId: String): TwoGisWorkersDTO? {
        return try {
            transaction {
                addLogger(StdOutSqlLogger)
                val result = TwoGisWorkersDB.selectAll().where { TwoGisWorkersDB.workerId.eq(workerId) }.single()
                TwoGisWorkersDTO(
                    twoGisText = result[twoGisText],
                    sendIfRating = result[sendIfRating]?.toList()?: listOf(1,2,3,4,5),
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
                TwoGisWorkersDB.deleteWhere {
                    TwoGisWorkersDB.workerId.eq(workerId)
                }
            }
            return true
        } catch (e: Exception) {
            Logging.e(tag, e.toString())
            return false
        }
    }

}
