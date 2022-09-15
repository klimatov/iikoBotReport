package domain.usecases

import data.repository.ReportRepositoryImpl

class GetReportList {
    fun execute(): MutableMap<String, String> {
        val reportRepository = ReportRepositoryImpl
        val doc = reportRepository.getList()
        val reportList = mutableMapOf<String, String>()

        if (doc != null) {
            doc.getElementsByTag("preset").forEach { reportList.put(it.id(), it.text()) }
        }
        return reportList
    }
}