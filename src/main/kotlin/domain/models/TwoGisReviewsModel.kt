package domain.models

import data.remoteAPI.twoGis.ReviewsGIS

data class TwoGisReviewsBundle(
    val twoGisCompanyData: TwoGisCompanyData,
    val twoGisReviewList: List<TwoGisReview> = emptyList()
)

data class TwoGisCompanyData(
    val id: String,
    val name: String? = null,
)

enum class TwoGisCompanyEnum(val twoGisCompanyData: TwoGisCompanyData) {
    MIRA(
        TwoGisCompanyData(
        id = "70000001030737926",
        name = "Купец&Ко на пр Мира"
        )
    ),
    YN25(TwoGisCompanyData(
        id = "70000001023172949",
        name = "Купец&Ко на Ярыгинской Набережной 25"
    )),
    YN23(TwoGisCompanyData(
        id = "70000001025624980",
        name = "Купец&Ко на Ярыгинской Набережной 23"
    ))
}

data class TwoGisReview(
    var objectId: String? = null,
    val id: String? = null,
    val regionId: Int? = null,
    val text: String? = null,
    val rating: Int? = null,
    val provider: String? = null,
    val source: String? = null,
    val isHidden: Boolean? = null,
    val hidingType: String? = null,
    val hidingReason: String? = null,
    val url: String? = null,
    val likesCount: Int? = null,
    val commentsCount: Int? = null,
    val dateCreated: String? = null,
    val dateEdited: String? = null,
    val onModeration: Boolean? = null,
    val isRated: Boolean? = null,
    val isVerified: Boolean? = null,
    val userGISid: String? = null,
    val userGISreviewsCount: Int? = null,
    val userGISfirstName: String? = null,
    val userGISlastName: String? = null,
    val userGISname: String? = null,
    val userGISprovider: String? = null,
    val userGISurl: String? = null,
    val userGISpublicId: String? = null
)

fun ReviewsGIS.mapToTwoGisReview(): TwoGisReview = TwoGisReview(
    objectId = objectGIS?.id,
    id = id,
    regionId = regionId,
    text = text,
    rating = rating,
    provider = provider,
    source = source,
    isHidden = isHidden,
    hidingType = hidingType,
    hidingReason = hidingReason,
    url = url,
    likesCount = likesCount,
    commentsCount = commentsCount,
    dateCreated = dateCreated,
    dateEdited = dateEdited,
    onModeration = onModeration,
    isRated = isRated,
    isVerified = isVerified,
    userGISid = user?.id,
    userGISreviewsCount = user?.reviewsCount,
    userGISfirstName = user?.firstName,
    userGISlastName = user?.lastName,
    userGISname = user?.name,
    userGISprovider = user?.provider,
    userGISurl = user?.url,
    userGISpublicId = user?.publicId,
)