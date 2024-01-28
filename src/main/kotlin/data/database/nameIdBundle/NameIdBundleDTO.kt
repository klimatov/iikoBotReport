package data.database.nameIdBundle

import kotlinx.serialization.Serializable
import models.BundleParam

@Serializable
data class NameIdBundleDTO(
    val botUserId: Int,
    val name: String,
    val telegramId: Long,
    val available: Boolean
)

fun BundleParam.mapToNameIdBundleDTO(): NameIdBundleDTO = NameIdBundleDTO(
    botUserId = botUserId,
    name = name,
    telegramId = telegramId,
    available = available
)

fun NameIdBundleDTO.mapToBundleParam(): BundleParam = BundleParam(
    botUserId = botUserId,
    name = name,
    telegramId = telegramId,
    available = available
)
