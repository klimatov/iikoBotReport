package data.fileProcessing

import models.IdAvailability
import models.BundleParam

object NameIdBundleRepository {
    private val tag = this::class.java.simpleName
    private var bundleParamList: MutableList<BundleParam> = mutableListOf()

    init {
        bundleParamList.add(
            BundleParam(
                1,
                "Иванов Иван",
                123456789,
                IdAvailability.NOT_CHECKED
            )
        )
        bundleParamList.add(
            BundleParam(
                2,
                "Чат для проверок",
                987654321,
                IdAvailability.NOT_CHECKED
            )
        )
    }
    fun get(): List<BundleParam> {
        return bundleParamList
    }

    fun set(newBundleParamList: List<BundleParam>) {
        bundleParamList = newBundleParamList.toMutableList()
    }

}