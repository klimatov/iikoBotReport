package data


import data.database.reportsWorkers.ReportsWorkersDB
import data.database.reportsWorkers.mapToReportWorkerParam
import data.database.reportsWorkers.mapToReportsWorkersDTO
import models.ReportWorkerParam

class ReportsRepository {
    private val tag = this::class.java.simpleName
    fun get(): MutableMap<String, ReportWorkerParam> {
        return ReportsWorkersDB.getAll().associate { it.workerId to it.mapToReportWorkerParam() }.toMutableMap()
    }

    fun set(workerList: MutableMap<String, ReportWorkerParam>?) {
        workerList?.forEach { (_, reportsWorkerParam) ->
            ReportsWorkersDB.insert(reportsWorkerParam.mapToReportsWorkersDTO())
        }
    }

    fun delete(workerId: String) {
        ReportsWorkersDB.deleteByWorkerId(workerId)
    }
}