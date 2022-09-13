package data.fileProcessing


import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import fileProcessing.FileOperations
import models.WorkerParam

class WorkersRepository {

    fun get(): MutableMap<String, WorkerParam>? {
        val serializedData = FileOperations().read("workers.cfg")
        val type = object : TypeToken<MutableMap<String, WorkerParam>>() {}.type
        var workerList =
            Gson().fromJson<MutableMap<String, WorkerParam>>(serializedData, type)
        if (workerList == null) workerList = mutableMapOf()
        return workerList
    }

    fun set(workerList: MutableMap<String, WorkerParam>?) {
        if (workerList != null) {
            val serializedData = Gson().toJson(workerList)
            FileOperations().write("workers.cfg", serializedData)
        }
    }
}