package data.database.nameIdBundle

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import utils.Logging

object NameIdBundleDB : Table("name_id_bundle") {
    private val tag = this::class.java.simpleName

    private val botUserId = NameIdBundleDB.integer("bot_user_id")
    private val name = NameIdBundleDB.varchar("name", 40)
    private val telegramId = NameIdBundleDB.long("telegram_id")
    private val available = NameIdBundleDB.bool("available")
    override val primaryKey = PrimaryKey(botUserId)

    fun insert(nameIdBundleDTO: NameIdBundleDTO): Boolean {
        try {
            transaction {
                addLogger(StdOutSqlLogger)
                NameIdBundleDB.upsert {
                    it[botUserId] = nameIdBundleDTO.botUserId
                    it[name] = nameIdBundleDTO.name
                    it[telegramId] = nameIdBundleDTO.telegramId
                    it[available] = nameIdBundleDTO.available
                }

            }
            return true
        } catch (e: Exception) {
            Logging.e(tag, e.toString())
            return false
        }
    }

    fun getAll(): List<NameIdBundleDTO> {
        return try {
            transaction {
                addLogger(StdOutSqlLogger)
                NameIdBundleDB.selectAll().toList().map {
                    NameIdBundleDTO(
                                botUserId = it[botUserId],
                                name = it[name],
                                telegramId = it[telegramId],
                                available = it[available],
                    )
                }
            }
        } catch (e: Exception) {
            Logging.e(tag, e.toString())
            listOf()
        }
    }

    fun deleteByUserId(userId: Int): Boolean {
        try {
            transaction {
                addLogger(StdOutSqlLogger)
                NameIdBundleDB.deleteWhere {
                    NameIdBundleDB.botUserId.eq(userId)
                }
            }
            return true
        } catch (e: Exception) {
            Logging.e(tag, e.toString())
            return false
        }
    }

    fun updateAll(nameIdBundleDTOList: List<NameIdBundleDTO>): Boolean {
        try {
            transaction {
                addLogger(StdOutSqlLogger)

                NameIdBundleDB.deleteAll()
                NameIdBundleDB.batchUpsert(nameIdBundleDTOList) {(botUserId, name, telegramId, available) ->
                        this[NameIdBundleDB.botUserId] = botUserId
                        this[NameIdBundleDB.name] = name
                        this[NameIdBundleDB.telegramId] = telegramId
                        this[NameIdBundleDB.available] = available
                }
            }
            return true
        } catch (e: Exception) {
            Logging.e(tag, e.toString())
            return false
        }
    }

}
