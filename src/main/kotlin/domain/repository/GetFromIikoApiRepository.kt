package domain.repository

import org.jsoup.nodes.Document

interface GetFromIikoApiRepository {
    fun getEmployees(): String?
}