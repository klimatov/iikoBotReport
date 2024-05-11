package data.database.birthdayWorkers

import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.json.json
import org.jetbrains.exposed.sql.transactions.transaction
import utils.Logging

object BirthdayWorkersDB : Table("birthdays_workers") {
    private val tag = this::class.java.simpleName
    private val birthdayText = BirthdayWorkersDB.text("birthday_text")
    private val sendBeforeDays = BirthdayWorkersDB.long("send_before_days")
    private val workerId = BirthdayWorkersDB.varchar("worker_id", 40)
    private val workerName = BirthdayWorkersDB.varchar("worker_name", 255)
    private val sendChatId = BirthdayWorkersDB.json<LongArray>("send_chat_id", Json.Default)
    private val sendWhenType = BirthdayWorkersDB.integer("send_when_type")
    private val sendPeriod = BirthdayWorkersDB.integer("send_period")
    private val sendTime = BirthdayWorkersDB.json<Array<String>>("send_time", Json.Default)
    private val sendWeekDay = BirthdayWorkersDB.json<IntArray>("send_week_day", Json.Default)
    private val sendMonthDay = BirthdayWorkersDB.json<IntArray>("send_month_day", Json.Default)
    private val nameInHeader = BirthdayWorkersDB.bool("name_in_header")
    private val workerIsActive = BirthdayWorkersDB.bool("worker_is_active")
    private val sendDateTimeList = BirthdayWorkersDB.json<Array<String>>("send_date_time_list", Json.Default)
    private val preliminarySendBeforeDays = BirthdayWorkersDB.long("preliminary_send_before_days").nullable()
    private val preliminarySendTime = BirthdayWorkersDB.varchar("preliminary_send_time", 5).nullable()

    override val primaryKey = PrimaryKey(workerId)

    fun insert(birthdayWorkersDTO: BirthdayWorkersDTO): Boolean {
        try {
            transaction {
                addLogger(StdOutSqlLogger)
                BirthdayWorkersDB.upsert {
                    it[birthdayText] = birthdayWorkersDTO.birthdayText
                    it[sendBeforeDays] = birthdayWorkersDTO.sendBeforeDays
                    it[workerId] = birthdayWorkersDTO.workerId
                    it[workerName] = birthdayWorkersDTO.workerName
                    it[sendChatId] = birthdayWorkersDTO.sendChatId.toLongArray()
                    it[sendWhenType] = birthdayWorkersDTO.sendWhenType
                    it[sendPeriod] = birthdayWorkersDTO.sendPeriod
                    it[sendTime] = birthdayWorkersDTO.sendTime.toTypedArray()
                    it[sendWeekDay] = birthdayWorkersDTO.sendWeekDay.toIntArray()
                    it[sendMonthDay] = birthdayWorkersDTO.sendMonthDay.toIntArray()
                    it[nameInHeader] = birthdayWorkersDTO.nameInHeader
                    it[workerIsActive] = birthdayWorkersDTO.workerIsActive
                    it[sendDateTimeList] = birthdayWorkersDTO.sendDateTimeList.toTypedArray()
                    it[preliminarySendBeforeDays] = birthdayWorkersDTO.preliminarySendBeforeDays
                    it[preliminarySendTime] = birthdayWorkersDTO.preliminarySendTime
                }
            }
            return true
        } catch (e: Exception) {
            Logging.e(tag, e.toString())
            return false
        }
    }

    fun getAll(): List<BirthdayWorkersDTO> {
        return try {
            transaction {
                addLogger(StdOutSqlLogger)
                BirthdayWorkersDB.selectAll().toList().map {
                    BirthdayWorkersDTO(
                        birthdayText = it[birthdayText],
                        sendBeforeDays = it[sendBeforeDays],
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
                BirthdayWorkersDB.deleteWhere {
                    BirthdayWorkersDB.workerId.eq(workerId)
                }
            }
            return true
        } catch (e: Exception) {
            Logging.e(tag, e.toString())
            return false
        }
    }

}
