package data

import data.database.DatabaseManage
import data.database.birthdayWorkers.BirthdayWorkersDB
import data.database.remindersWorkers.RemindersWorkersDB
import data.database.reportsWorkers.ReportsWorkersDB
import data.database.reviewsWorkers.ReviewsWorkersDB

class DatabaseRepository {
    private val tag = this::class.java.simpleName
    fun checkAndCreateTables() {
        val databaseManage = DatabaseManage()
        databaseManage.createTable(ReviewsWorkersDB)
        databaseManage.createTable(BirthdayWorkersDB)
        databaseManage.createTable(RemindersWorkersDB)
        databaseManage.createTable(ReportsWorkersDB)
    }
}