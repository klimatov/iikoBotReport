package data.database

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import utils.Logging

class DatabaseManage {
    private val tag = this::class.java.simpleName

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
}