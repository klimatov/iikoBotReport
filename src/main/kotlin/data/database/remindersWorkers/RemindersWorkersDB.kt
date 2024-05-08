package data.database.remindersWorkers

import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.json.json
import org.jetbrains.exposed.sql.transactions.transaction
import utils.Logging

object RemindersWorkersDB : Table("reminders_workers") {
    private val tag = this::class.java.simpleName
    private val reminderText = RemindersWorkersDB.text("reminder_text")
    private val workerId = RemindersWorkersDB.varchar("worker_id", 40)
    private val workerName = RemindersWorkersDB.varchar("worker_name", 255)
    private val sendChatId = RemindersWorkersDB.json<LongArray>("send_chat_id", Json.Default)
    private val sendWhenType = RemindersWorkersDB.integer("send_when_type")
    private val sendPeriod = RemindersWorkersDB.integer("send_period")
    private val sendTime = RemindersWorkersDB.json<Array<String>>("send_time", Json.Default)
    private val sendWeekDay = RemindersWorkersDB.json<IntArray>("send_week_day", Json.Default)
    private val sendMonthDay = RemindersWorkersDB.json<IntArray>("send_month_day", Json.Default)
    private val nameInHeader = RemindersWorkersDB.bool("name_in_header")
    private val workerIsActive = RemindersWorkersDB.bool("worker_is_active")
    private val sendDateTimeList = RemindersWorkersDB.json<Array<String>>("send_date_time_list", Json.Default)
    private val preliminarySendBeforeDays = RemindersWorkersDB.long("preliminary_send_before_days").nullable()
    private val preliminarySendTime = RemindersWorkersDB.varchar("preliminary_send_time", 5).nullable()

    override val primaryKey = PrimaryKey(workerId)

    fun insert(remindersWorkersDTO: RemindersWorkersDTO): Boolean {
        try {
            transaction {
                addLogger(StdOutSqlLogger)
                RemindersWorkersDB.upsert {
                    it[reminderText] = remindersWorkersDTO.reminderText
                    it[workerId] = remindersWorkersDTO.workerId
                    it[workerName] = remindersWorkersDTO.workerName
                    it[sendChatId] = remindersWorkersDTO.sendChatId.toLongArray()
                    it[sendWhenType] = remindersWorkersDTO.sendWhenType
                    it[sendPeriod] = remindersWorkersDTO.sendPeriod
                    it[sendTime] = remindersWorkersDTO.sendTime.toTypedArray()
                    it[sendWeekDay] = remindersWorkersDTO.sendWeekDay.toIntArray()
                    it[sendMonthDay] = remindersWorkersDTO.sendMonthDay.toIntArray()
                    it[nameInHeader] = remindersWorkersDTO.nameInHeader
                    it[workerIsActive] = remindersWorkersDTO.workerIsActive
                    it[sendDateTimeList] = remindersWorkersDTO.sendDateTimeList.toTypedArray()
                    it[preliminarySendBeforeDays] = remindersWorkersDTO.preliminarySendBeforeDays
                    it[preliminarySendTime] = remindersWorkersDTO.preliminarySendTime
                }
            }
            return true
        } catch (e: Exception) {
            Logging.e(tag, e.toString())
            return false
        }
    }

    fun getAll(): List<RemindersWorkersDTO> {
        return try {
            transaction {
                addLogger(StdOutSqlLogger)
                RemindersWorkersDB.selectAll().toList().map {
                    RemindersWorkersDTO(
                        reminderText = it[reminderText],
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
                        sendDateTimeList = it[sendDateTimeList].toList(),
                        preliminarySendBeforeDays = it[preliminarySendBeforeDays] ?: 0,
                        preliminarySendTime = it[preliminarySendTime] ?: "10:00",
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
                RemindersWorkersDB.deleteWhere {
                    RemindersWorkersDB.workerId.eq(workerId)
                }
            }
            return true
        } catch (e: Exception) {
            Logging.e(tag, e.toString())
            return false
        }
    }

}
