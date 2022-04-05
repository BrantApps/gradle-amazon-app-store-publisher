package io.github.angelstudios.api.model

@kotlinx.serialization.Serializable
data class Token(
    val access_token: String,
    val scope: String,
    val token_type: String,
    val expires_in: Int
)
