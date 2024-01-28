package data.database.reportsWorkers

import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.json.json
import org.jetbrains.exposed.sql.transactions.transaction
import utils.Logging

object ReportsWorkersDB : Table("reports_workers") {
    private val tag = this::class.java.simpleName

    private val reportId = ReportsWorkersDB.varchar("report_id", 40)
    private val reportPeriod = ReportsWorkersDB.integer("report_period")
    private val messageHeader = ReportsWorkersDB.bool("message_header")
    private val messageSuffix = ReportsWorkersDB.json<Map<Int, String>>("message_suffix", Json.Default)
    private val messageAmount = ReportsWorkersDB.integer("message_amount")
    private val messageWordLimit = ReportsWorkersDB.json<Map<Int, Int>>("message_word_limit", Json.Default)
    private val workerId = ReportsWorkersDB.varchar("worker_id", 40)
    private val workerName = ReportsWorkersDB.varchar("worker_name", 255)
    private val sendChatId = ReportsWorkersDB.json<LongArray>("send_chat_id", Json.Default)
    private val sendWhenType = ReportsWorkersDB.integer("send_when_type")
    private val sendPeriod = ReportsWorkersDB.integer("send_period")
    private val sendTime = ReportsWorkersDB.json<Array<String>>("send_time", Json.Default)
    private val sendWeekDay = ReportsWorkersDB.json<IntArray>("send_week_day", Json.Default)
    private val sendMonthDay = ReportsWorkersDB.json<IntArray>("send_month_day", Json.Default)
    private val nameInHeader = ReportsWorkersDB.bool("name_in_header")
    private val workerIsActive = ReportsWorkersDB.bool("worker_is_active")
    private val sendDateTimeList = ReportsWorkersDB.json<Array<String>>("send_date_time_list", Json.Default)
    override val primaryKey = PrimaryKey(workerId)

    fun insert(reportsWorkersDTO: ReportsWorkersDTO): Boolean {
        try {
            transaction {
                addLogger(StdOutSqlLogger)
                ReportsWorkersDB.upsert {
                    it[reportId] = reportsWorkersDTO.reportId
                    it[reportPeriod] = reportsWorkersDTO.reportPeriod
                    it[messageHeader] = reportsWorkersDTO.messageHeader
                    it[messageSuffix] = reportsWorkersDTO.messageSuffix
                    it[messageAmount] = reportsWorkersDTO.messageAmount
                    it[messageWordLimit] = reportsWorkersDTO.messageWordLimit
                    it[workerId] = reportsWorkersDTO.workerId
                    it[workerName] = reportsWorkersDTO.workerName
                    it[sendChatId] = reportsWorkersDTO.sendChatId.toLongArray()
                    it[sendWhenType] = reportsWorkersDTO.sendWhenType
                    it[sendPeriod] = reportsWorkersDTO.sendPeriod
                    it[sendTime] = reportsWorkersDTO.sendTime.toTypedArray()
                    it[sendWeekDay] = reportsWorkersDTO.sendWeekDay.toIntArray()
                    it[sendMonthDay] = reportsWorkersDTO.sendMonthDay.toIntArray()
                    it[nameInHeader] = reportsWorkersDTO.nameInHeader
                    it[workerIsActive] = reportsWorkersDTO.workerIsActive
                    it[sendDateTimeList] = reportsWorkersDTO.sendDateTimeList.toTypedArray()
                }

            }
            return true
        } catch (e: Exception) {
            Logging.e(tag, e.toString())
            return false
        }
    }

    fun getAll(): List<ReportsWorkersDTO> {
        return try {
            transaction {
                addLogger(StdOutSqlLogger)
                ReportsWorkersDB.selectAll().toList().map {
                    ReportsWorkersDTO(
                        reportId = it[reportId],
                        reportPeriod = it[reportPeriod],
                        messageHeader = it[messageHeader],
                        messageSuffix = it[messageSuffix],
                        messageAmount = it[messageAmount],
                        messageWordLimit = it[messageWordLimit],
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

    fun deleteByWorkerId(workerId: String): Boolean {
        try {
            transaction {
                addLogger(StdOutSqlLogger)
                ReportsWorkersDB.deleteWhere {
                    ReportsWorkersDB.workerId.eq(workerId)
                }
            }
            return true
        } catch (e: Exception) {
            Logging.e(tag, e.toString())
            return false
        }
    }

}
