package data

import data.database.remindersWorkers.RemindersWorkersDB
import data.database.remindersWorkers.mapToReminderWorkerParam
import data.database.remindersWorkers.mapToRemindersWorkersDTO
import models.ReminderWorkerParam

class RemindersRepository {
    private val tag = this::class.java.simpleName
    fun get(): MutableMap<String, ReminderWorkerParam> {
        return RemindersWorkersDB.getAll().associate { it.workerId to it.mapToReminderWorkerParam() }.toMutableMap()
    }

    fun set(workerList: MutableMap<String, ReminderWorkerParam>?) {
        workerList?.forEach { (_, reminderWorkerParam) ->
            RemindersWorkersDB.insert(reminderWorkerParam.mapToRemindersWorkersDTO())
        }
    }
    fun delete(workerId: String) {
        RemindersWorkersDB.deleteByWorkerId(workerId)
    }
}