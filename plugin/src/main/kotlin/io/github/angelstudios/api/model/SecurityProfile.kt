package io.github.angelstudios.api.model

@kotlinx.serialization.Serializable
data class SecurityProfile(
    val grant_type: String,
    val client_id: String,
    val client_secret: String,
    val scope: String
)
