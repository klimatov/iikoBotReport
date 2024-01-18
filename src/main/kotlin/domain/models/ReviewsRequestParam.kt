package domain.models

import kotlinx.serialization.Serializable

@Serializable
data class ReviewsRequestParam(
        val clientID: String? = null,
        val employees: String? = null,
        val length: Int,
        val offset: Int = 0,
        val orderTypes: String? = null,
        val outlets: String? = null,
        val partnerID: String,
        val periodFrom: String,
        val periodTo: String? =null,
        val processed: String? = null,
        val ratings: String? = null,
        val sort: Int = 1,
        val withComment: Boolean
)
