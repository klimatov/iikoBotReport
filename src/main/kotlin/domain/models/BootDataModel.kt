package domain.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BootDataModel(
    @Expose @SerializedName("user") var user: User? = User(),
    @Expose @SerializedName("fileServerURL") var fileServerURL: String? = null,
    @Expose @SerializedName("imageServerURL") var imageServerURL: String? = null
)

data class User(
    @Expose @SerializedName("id") var id: Int? = null,
    @Expose @SerializedName("login") var login: String? = null,
    @Expose @SerializedName("email") var email: String? = null,
    @Expose @SerializedName("name") var name: String? = null,
    @Expose @SerializedName("language") var language: String? = null,
    @Expose @SerializedName("country") var country: String? = null,
    @Expose @SerializedName("timeZone") var timeZone: String? = null,
    @Expose @SerializedName("partnerId") var partnerId: Int? = null,
    @Expose @SerializedName("partner") var partner: Partner? = Partner(),
    @Expose @SerializedName("partnerLogo") var partnerLogo: String? = null,
    @Expose @SerializedName("timeZoneOffset") var timeZoneOffset: String? = null,
    @Expose @SerializedName("partnerTitle") var partnerTitle: String? = null,
    @Expose @SerializedName("hidden") var hidden: Boolean? = null,
    @Expose @SerializedName("permissions") var permissions: List<Int> = listOf(),
    @Expose @SerializedName("languages") var languages: List<String> = listOf()
)

data class Partner(
    @Expose @SerializedName("id") var id: Int? = null,
    @Expose @SerializedName("title") var title: String? = null,
    @Expose @SerializedName("defaultUserId") var defaultUserId: Int? = null,
    @Expose @SerializedName("defaultLanguageId") var defaultLanguageId: Int? = null,
    @Expose @SerializedName("defaultLanguage") var defaultLanguage: String? = null,
    @Expose @SerializedName("currency") var currency: Currency? = Currency(),
    @Expose @SerializedName("countryId") var countryId: Int? = null,
    @Expose @SerializedName("features") var features: List<Int> = listOf(),
    @Expose @SerializedName("languages") var languages: List<String> = listOf(),
    @Expose @SerializedName("districts") var districts: List<Districts> = listOf(),
    @Expose @SerializedName("outlets") var outlets: List<Outlets> = listOf(),
    @Expose @SerializedName("employees") var employees: List<Employees> = listOf(),
    @Expose @SerializedName("validators") var validators: List<Validators> = listOf(),
    @Expose @SerializedName("bonusSchemes") var bonusSchemes: List<BonusSchemes> = listOf(),
    @Expose @SerializedName("validatorQuestions") var validatorQuestions: List<String> = listOf(),
    @Expose @SerializedName("supportedProcessTypes") var supportedProcessTypes: List<String> = listOf(),
    @Expose @SerializedName("supportedChannels") var supportedChannels: List<String> = listOf(),
    @Expose @SerializedName("sections") var sections: List<Sections> = listOf(),
    @Expose @SerializedName("forms") var forms: List<Forms> = listOf(),
    @Expose @SerializedName("menuItemSources") var menuItemSources: List<String> = listOf()
)

data class Currency(
    @Expose @SerializedName("id") var id: Int? = null,
    @Expose @SerializedName("multiplier") var multiplier: Int? = null,
    @Expose @SerializedName("shortName") var shortName: String? = null,
    @Expose @SerializedName("symbol") var symbol: String? = null
)

data class Districts(
    @Expose @SerializedName("id") var id: Int? = null,
    @Expose @SerializedName("partnerID") var partnerID: Int? = null,
    @Expose @SerializedName("priority") var priority: Int? = null,
    @Expose @SerializedName("type") var type: Int? = null,
    @Expose @SerializedName("title") var title: Title? = Title(),
    @Expose @SerializedName("outlets") var outlets: List<Int> = listOf()
)

data class Outlets(
    @Expose @SerializedName("id") var id: Int? = null,
    @Expose @SerializedName("partnerID") var partnerID: Int? = null,
    @Expose @SerializedName("hidden") var hidden: Boolean? = null,
    @Expose @SerializedName("type") var type: Int? = null,
    @Expose @SerializedName("publicState") var publicState: Int? = null,
    @Expose @SerializedName("allowed") var allowed: Boolean? = null,
    @Expose @SerializedName("digitalOrderingEnabled") var digitalOrderingEnabled: Boolean? = null,
    @Expose @SerializedName("name") var name: Name? = Name(),
    @Expose @SerializedName("address") var address: Address? = Address(),
    @Expose @SerializedName("businessHours") var businessHours: BusinessHours? = BusinessHours(),
    @Expose @SerializedName("latitude") var latitude: Double? = null,
    @Expose @SerializedName("longitude") var longitude: Double? = null,
    @Expose @SerializedName("timeZoneName") var timeZoneName: String? = null
)

data class Name (
    @Expose @SerializedName("1" ) var name : String? = null
)

data class Address (
    @Expose @SerializedName("1" ) var adress : String? = null
)

data class BusinessHours (
    @Expose @SerializedName("1" ) var bussinessHours : String? = null
)

data class Employees(
    @Expose @SerializedName("id") var id: Int? = null,
    @Expose @SerializedName("type") var type: Int? = null,
    @Expose @SerializedName("name") var name: String? = null,
    @Expose @SerializedName("active") var active: Boolean? = null
)

data class Validators(
    @Expose @SerializedName("id") var id: Int? = null,
    @Expose @SerializedName("name") var name: String? = null,
    @Expose @SerializedName("outletID") var outletID: Int? = null
)

data class BonusSchemes(
    @Expose @SerializedName("id") var id: Int? = null,
    @Expose @SerializedName("type") var type: Int? = null,
    @Expose @SerializedName("title") var title: String? = null,
    @Expose @SerializedName("percents") var percents: Int? = null,
    @Expose @SerializedName("reward") var reward: Int? = null,
    @Expose @SerializedName("step") var step: Int? = null,
    @Expose @SerializedName("default") var default: Boolean? = null
)

data class Sections(
    @Expose @SerializedName("id") var id: Int? = null,
    @Expose @SerializedName("title") var title: Title? = Title(),
    @Expose @SerializedName("type") var type: Int? = null,
    @Expose @SerializedName("priority") var priority: Int? = null,
    @Expose @SerializedName("hidden") var hidden: Boolean? = null
)

data class Title(
    @Expose @SerializedName("ru") var ru: String? = null

)

data class Forms(
    @Expose @SerializedName("id") var id: Int? = null,
    @Expose @SerializedName("type") var type: Int? = null,
    @Expose @SerializedName("title") var title: Title? = Title()
)