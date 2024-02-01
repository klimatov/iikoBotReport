package data

import data.database.twoGisData.TwoGisDataDB
import data.database.twoGisData.mapToTwoGisDataDTO
import data.database.twoGisData.mapToTwoGisDataParam
import models.TwoGisDataParam

object TwoGisDataRepository {
    private val tag = this::class.java.simpleName
    fun get(): List<TwoGisDataParam> {
        return TwoGisDataDB.getAll().map { it.mapToTwoGisDataParam() }.toList()
    }

    fun set(twoGisDataParamList: List<TwoGisDataParam>) {
        twoGisDataParamList.forEach { reviewsData ->
            TwoGisDataDB.insert(reviewsData.mapToTwoGisDataDTO())
        }
    }

    fun setByWorkerId(twoGisDataParam: TwoGisDataParam) {
        TwoGisDataDB.insert(twoGisDataParam.mapToTwoGisDataDTO())
    }

    fun delete(workerId: String) {
        TwoGisDataDB.deleteByWorkerId(workerId)
    }

    fun getByWorkerId(workerId: String): TwoGisDataParam? =
        TwoGisDataDB.getByWorkerId(workerId)?.mapToTwoGisDataParam()

}