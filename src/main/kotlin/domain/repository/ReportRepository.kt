package domain.repository

import domain.models.ReportParam
import org.jsoup.nodes.Document

interface ReportRepository {
    fun get(reportParam: ReportParam): Document?
}