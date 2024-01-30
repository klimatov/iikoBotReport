package data.remoteAPI.twoGis

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class TwoGisReviewsDTO(
    @Expose @SerializedName("reviews") var reviews: List<ReviewsGIS> = listOf()
)
data class ReviewsGIS(
    @Expose @SerializedName("id") var id: String? = null,
    @Expose @SerializedName("region_id") var regionId: Int? = null,
    @Expose @SerializedName("text") var text: String? = null,
    @Expose @SerializedName("rating") var rating: Int? = null,
    @Expose @SerializedName("provider") var provider: String? = null,
    @Expose @SerializedName("source") var source: String? = null,
    @Expose @SerializedName("is_hidden") var isHidden: Boolean? = null,
    @Expose @SerializedName("hiding_type") var hidingType: String? = null,
    @Expose @SerializedName("hiding_reason") var hidingReason: String? = null,
    @Expose @SerializedName("url") var url: String? = null,
    @Expose @SerializedName("likes_count") var likesCount: Int? = null,
    @Expose @SerializedName("comments_count") var commentsCount: Int? = null,
    @Expose @SerializedName("date_created") var dateCreated: String? = null,
    @Expose @SerializedName("date_edited") var dateEdited: String? = null,
    @Expose @SerializedName("object") var objectGIS: ObjectGIS? = ObjectGIS(),
    @Expose @SerializedName("user") var user: UserGIS? = UserGIS(),
    @Expose @SerializedName("official_answer") var officialAnswer: OfficialAnswer? = OfficialAnswer(),
//    @Expose @SerializedName("photos") var photos: List<String> = listOf(),
    @Expose @SerializedName("on_moderation") var onModeration: Boolean? = null,
    @Expose @SerializedName("is_rated") var isRated: Boolean? = null,
    @Expose @SerializedName("is_verified") var isVerified: Boolean? = null
)

data class ObjectGIS(
    @Expose @SerializedName("id") var id: String? = null,
    @Expose @SerializedName("type") var type: String? = null
)

data class UserGIS(
    @Expose @SerializedName("id") var id: String? = null,
    @Expose @SerializedName("reviews_count") var reviewsCount: Int? = null,
    @Expose @SerializedName("first_name") var firstName: String? = null,
    @Expose @SerializedName("last_name") var lastName: String? = null,
    @Expose @SerializedName("name") var name: String? = null,
    @Expose @SerializedName("provider") var provider: String? = null,
    @Expose @SerializedName("url") var url: String? = null,
    @Expose @SerializedName("public_id") var publicId: String? = null
)

data class OfficialAnswer(
    @Expose @SerializedName("id") var id: String? = null,
    @Expose @SerializedName("org_name") var orgName: String? = null,
    @Expose @SerializedName("text") var text: String? = null,
    @Expose @SerializedName("date_created") var dateCreated: String? = null,
)

