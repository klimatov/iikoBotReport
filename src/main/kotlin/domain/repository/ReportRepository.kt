package domain.repository

import domain.models.RequestParam
import org.jsoup.nodes.Document

interface ReportRepository {
    fun get(requestParam: RequestParam): Document?
    fun getList(): Document?
}