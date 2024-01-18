package domain.repository

import domain.models.ReportRequestParam
import org.jsoup.nodes.Document

interface ReportRepository {
    fun get(reportRequestParam: ReportRequestParam): Document?
    fun getList(): Document?
}