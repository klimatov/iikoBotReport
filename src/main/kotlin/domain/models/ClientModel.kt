package domain.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ClientModel(
    @Expose @SerializedName("data") var clientData: ClientData? = ClientData()
)

data class FavouriteOutlets(
    @Expose @SerializedName("id") var id: Int? = null,
    @Expose @SerializedName("visitsCount") var visitsCount: Int? = null,
    @Expose @SerializedName("spentSum") var spentSum: Int? = null
)

data class Answers(
    @Expose @SerializedName("question") var question: Int? = null,
    @Expose @SerializedName("type") var type: Int? = null,
    @Expose @SerializedName("value") var value: String? = null
)

data class Devices(
    @Expose @SerializedName("platform") var platform: String? = null,
    @Expose @SerializedName("model") var model: String? = null,
    @Expose @SerializedName("installationDatetime") var installationDatetime: String? = null
)

data class Client(
    @Expose @SerializedName("id") var id: Int? = null,
    @Expose @SerializedName("age") var age: Int? = null,
    @Expose @SerializedName("state") var state: Int? = null,
    @Expose @SerializedName("role") var role: Int? = null,
    @Expose @SerializedName("balance") var balance: Int? = null,
    @Expose @SerializedName("email") var email: String? = null,
    @Expose @SerializedName("phone") var phone: String? = null,
    @Expose @SerializedName("dateOfBirth") var dateOfBirth: String? = null,
    @Expose @SerializedName("installationDatetime") var installationDatetime: String? = null,
    @Expose @SerializedName("lastVisitedTime") var lastVisitedTime: String? = null,
    @Expose @SerializedName("language") var language: Int? = null,
    @Expose @SerializedName("firstName") var firstName: String? = null,
    @Expose @SerializedName("fullName") var fullName: String? = null,
    @Expose @SerializedName("visits") var visits: Int? = null,
    @Expose @SerializedName("moneySpent") var moneySpent: Int? = null,
    @Expose @SerializedName("invitedBy") var invitedBy: Int? = null,
    @Expose @SerializedName("bonusSchemeID") var bonusSchemeID: Int? = null,
    @Expose @SerializedName("subscribedToPush") var subscribedToPush: Boolean? = null,
    @Expose @SerializedName("favouriteOutlets") var favouriteOutlets: List<FavouriteOutlets> = listOf(),
    @Expose @SerializedName("answers") var answers: List<Answers> = listOf(),
    @Expose @SerializedName("devices") var devices: List<Devices> = listOf(),
    @Expose @SerializedName("socialNetworks") var socialNetworks: List<String> = listOf()
)

data class ClientData(
    @Expose @SerializedName("client") var client: Client? = Client()
)