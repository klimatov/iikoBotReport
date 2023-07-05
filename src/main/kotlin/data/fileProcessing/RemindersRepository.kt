package data.fileProcessing

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import models.ReminderWorkerParam
import utils.Logging

class RemindersRepository {
    private val tag = this::class.java.simpleName
    fun get(): MutableMap<String, ReminderWorkerParam> {
        val serializedData = FileOperations().read("reminders.cfg")
        Logging.d(tag,serializedData)
        val type = object : TypeToken<MutableMap<String, ReminderWorkerParam>>() {}.type
        var workerList =
            Gson().fromJson<MutableMap<String, ReminderWorkerParam>>(serializedData, type)
        if (workerList == null) workerList = mutableMapOf()
        return workerList
    }

    fun set(workerList: MutableMap<String, ReminderWorkerParam>?) {
        if (workerList != null) {
            val serializedData = Gson().toJson(workerList)
            FileOperations().write("reminders.cfg", serializedData)
        }
    }
}