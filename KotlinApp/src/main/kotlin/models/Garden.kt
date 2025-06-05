package models

import kotlinx.serialization.Serializable

@Serializable
data class Tile(
    val x: Int,
    val y: Int,
    val type: String,
    val crop: String? = null,
    val plantedDate: String? = null,
    val wateredDate: String? = null,
    val _id: String? = null
)

@Serializable
data class Garden(
    val _id: String,
    val name: String,
    val owner: String,
    val width: Int,
    val height: Int,
    val location: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val elements: List<Tile> = emptyList(),
    val createdAt: String? = null,
    val updatedAt: String? = null
)