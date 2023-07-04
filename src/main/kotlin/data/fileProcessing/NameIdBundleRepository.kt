package data.fileProcessing

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import models.IdAvailability
import models.BundleParam

object NameIdBundleRepository {
    private val tag = this::class.java.simpleName

    //    private var bundleParamList: MutableList<BundleParam> = mutableListOf()
//
//    init {
//        bundleParamList.add(
//            BundleParam(
//                1,
//                "Иванов Иван",
//                123456789,
//                IdAvailability.NOT_CHECKED
//            )
//        )
//        bundleParamList.add(
//            BundleParam(
//                2,
//                "Чат для проверок",
//                987654321,
//                IdAvailability.NOT_CHECKED
//            )
//        )
//    }
    fun get(): List<BundleParam> {
        val serializedData = FileOperations().read("nameidbundle.cfg")
        val type = object : TypeToken<List<BundleParam>>() {}.type
        var bundleParamList = Gson().fromJson<List<BundleParam>>(serializedData, type)
        if (bundleParamList == null) bundleParamList = listOf()
        return bundleParamList
    }

    fun set(newBundleParamList: List<BundleParam>) {
        val serializedData = Gson().toJson(newBundleParamList)
        FileOperations().write("nameidbundle.cfg", serializedData)
    }

}