package data.fileProcessing


import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import models.ReportWorkerParam

class ReportsRepository {
    private val tag = this::class.java.simpleName
    fun get(): MutableMap<String, ReportWorkerParam> {
        val serializedData = FileOperations().read("workers.cfg")
        val type = object : TypeToken<MutableMap<String, ReportWorkerParam>>() {}.type
        var workerList =
            Gson().fromJson<MutableMap<String, ReportWorkerParam>>(serializedData, type)
        if (workerList == null) workerList = mutableMapOf()
        return workerList
    }

    fun set(workerList: MutableMap<String, ReportWorkerParam>?) {
        if (workerList != null) {
            val serializedData = Gson().toJson(workerList)
            FileOperations().write("workers.cfg", serializedData)
        }
    }
}