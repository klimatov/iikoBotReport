package data.fileProcessing

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import models.BirthdayWorkerParam
import models.ReminderWorkerParam
import utils.Logging

class BirthdayRepository {
    private val tag = this::class.java.simpleName
    fun get(): MutableMap<String, BirthdayWorkerParam> {
        val serializedData = FileOperations().read("birthdays.cfg")
        val type = object : TypeToken<MutableMap<String, BirthdayWorkerParam>>() {}.type
        var workerList =
            Gson().fromJson<MutableMap<String, BirthdayWorkerParam>>(serializedData, type)
        if (workerList == null) workerList = mutableMapOf()
        return workerList
    }

    fun set(workerList: MutableMap<String, BirthdayWorkerParam>?) {
        if (workerList != null) {
            val serializedData = Gson().toJson(workerList)
            FileOperations().write("birthdays.cfg", serializedData)
        }
    }
}