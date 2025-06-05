package models

import kotlinx.serialization.Serializable

@Serializable
data class Watering(
    val frequency: String,
    val amount: Double
)

@Serializable
data class CompanionCrop(
    val _id: String,
    val name: String,
    val latinName: String
)

@Serializable
data class Crop(
    val _id: String,
    val name: String,
    val latinName: String,
    val goodCompanions: List<CompanionCrop>,
    val badCompanions: List<CompanionCrop>,
    val plantingMonth: String,
    val watering: Watering,
    val imageSrc: String? = null
)

@Serializable
data class Amount(
    val amount: Int,
)


@Serializable
data class GenerateCropResponse(
    val success: Boolean,
    val crops: List<GeneratedCrop>
)

@Serializable
data class GeneratedCrop(
    val name: String,
    val latinName: String,
    val goodCompanions: List<String>,
    val badCompanions: List<String>,
    val plantingMonth: String,
    val watering: Watering
)

