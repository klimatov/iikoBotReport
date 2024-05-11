package domain.usecases

import data.remoteAPI.iiko.ReportRepositoryImpl

class GetReportList {
    private val tag = this::class.java.simpleName
    fun execute(): MutableMap<String, String> {
        val reportRepository = ReportRepositoryImpl
        val doc = reportRepository.getList()
        val reportList = mutableMapOf<String, String>()

        doc?.getElementsByTag("preset")?.forEach { reportList[it.id()] = it.text() }
        return reportList
    }
}