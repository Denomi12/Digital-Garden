package models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val _id: String,
    val username: String,
    val email: String
)
