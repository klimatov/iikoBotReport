package domain.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ReviewsModel(
    @Expose @SerializedName("data") var data: Data? = Data()
)

data class Data(
    @Expose @SerializedName("reviews") var reviews: List<Reviews> = listOf()
)

data class Reviews(
    @Expose @SerializedName("id") var id: Int? = null,
    @Expose @SerializedName("type") var type: Int? = null,
    @Expose @SerializedName("state") var state: Int? = null,
    @Expose @SerializedName("text") var text: String? = null,
    @Expose @SerializedName("rating") var rating: Int? = null,
    @Expose @SerializedName("client") var client: Int? = null,
    @Expose @SerializedName("createdTimestamp") var createdTimestamp: String? = null,
    @Expose @SerializedName("processed") var processed: Boolean? = null,
    @Expose @SerializedName("transaction") var transaction: Transaction? = Transaction(),
    @Expose @SerializedName("outlet") var outlet: Int? = null,
    @Expose @SerializedName("order") var order: Int? = null
)

data class Transaction(
    @Expose @SerializedName("id") var id: Int? = null,
    @Expose @SerializedName("type") var type: Int? = null,
    @Expose @SerializedName("state") var state: Int? = null,
    @Expose @SerializedName("sum") var sum: Int? = null,
    @Expose @SerializedName("client") var client: Int? = null,
    @Expose @SerializedName("purchaseAmount") var purchaseAmount: Int? = null,
    @Expose @SerializedName("validatedTimestamp") var validatedTimestamp: String? = null,
    @Expose @SerializedName("outlet") var outlet: Int? = null,
    @Expose @SerializedName("validator") var validator: Int? = null,
    @Expose @SerializedName("coupon") var coupon: Int? = null,
    @Expose @SerializedName("validationID") var validationID: Int? = null,
//    @SerializedName("validatorAnswers") var validatorAnswers: ValidatorAnswers? = ValidatorAnswers()
)

//class ValidatorAnswers()
