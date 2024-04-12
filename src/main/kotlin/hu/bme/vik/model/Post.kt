package hu.bme.vik.model

import kotlinx.serialization.Serializable

@Serializable
data class Post(
    val imageUrl: String,
    val description: String
)
