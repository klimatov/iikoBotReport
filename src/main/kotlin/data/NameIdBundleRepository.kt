package data

import data.database.nameIdBundle.NameIdBundleDB
import data.database.nameIdBundle.mapToBundleParam
import data.database.nameIdBundle.mapToNameIdBundleDTO
import models.BundleParam

object NameIdBundleRepository {
    private val tag = this::class.java.simpleName

    fun get(): List<BundleParam> {
        return NameIdBundleDB.getAll().map { it.mapToBundleParam() }.toList()
    }

    fun set(newBundleParamList: List<BundleParam>) {
        newBundleParamList.forEach { bundleParam ->
            NameIdBundleDB.insert(bundleParam.mapToNameIdBundleDTO())
        }
    }

    fun updateAll(newBundleParamList: List<BundleParam>) {
        NameIdBundleDB.updateAll(newBundleParamList.map { it.mapToNameIdBundleDTO() }.toList())
    }

    fun delete(userId: Int) {
        NameIdBundleDB.deleteByUserId(userId)
    }

}