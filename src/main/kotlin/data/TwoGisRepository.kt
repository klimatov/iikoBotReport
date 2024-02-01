package data

import data.database.twoGisWorkers.TwoGisWorkersDB
import data.database.twoGisWorkers.mapToTwoGisWorkerParam
import data.database.twoGisWorkers.mapToTwoGisWorkersDTO
import models.TwoGisWorkerParam

class TwoGisRepository {
    private val tag = this::class.java.simpleName
    fun get(): MutableMap<String, TwoGisWorkerParam> {
        return TwoGisWorkersDB.getAll().associate { it.workerId to it.mapToTwoGisWorkerParam() }.toMutableMap()
    }

    fun set(workerList: MutableMap<String, TwoGisWorkerParam>?) {
        workerList?.forEach { (_, twoGisWorkerParam) ->
            TwoGisWorkersDB.insert(twoGisWorkerParam.mapToTwoGisWorkersDTO())
        }
    }

    fun delete(workerId: String) {
        TwoGisWorkersDB.deleteByWorkerId(workerId)
    }

    // TODO: методы для апдейта отдельных воркеров и т.п.

    /*    fun getShownList(): MutableMap<Int, String> {
            val serializedData = FileOperations().read("shownreviews.cfg")
            val type = object : TypeToken<MutableMap<Int, String>>() {}.type
            var shownReviews =
                Gson().fromJson<MutableMap<Int, String>>(serializedData, type)
            if (shownReviews == null) shownReviews = mutableMapOf()
            return shownReviews
        }

        fun setShownList(shownReviews: MutableMap<Int, String>?) {
            if (shownReviews != null) {
                val serializedData = Gson().toJson(shownReviews)
                FileOperations().write("shownreviews.cfg", serializedData)
            }
        }*/
}