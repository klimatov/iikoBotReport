package data

import data.database.birthdayWorkers.BirthdayWorkersDB
import data.database.birthdayWorkers.mapToBirthdayWorkerParam
import data.database.birthdayWorkers.mapToBirthdayWorkersDTO
import models.BirthdayWorkerParam

class BirthdayRepository {
    private val tag = this::class.java.simpleName
    fun get(): MutableMap<String, BirthdayWorkerParam> {
        return BirthdayWorkersDB.getAll().associate { it.workerId to it.mapToBirthdayWorkerParam() }.toMutableMap()
    }

    fun set(workerList: MutableMap<String, BirthdayWorkerParam>?) {
        workerList?.forEach { (_, birthdayWorkerParam) ->
            BirthdayWorkersDB.insert(birthdayWorkerParam.mapToBirthdayWorkersDTO())
        }
    }

    fun delete(workerId: String) {
        BirthdayWorkersDB.deleteByWorkerId(workerId)
    }

}