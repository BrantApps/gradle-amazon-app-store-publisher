package io.github.angelstudios.api.model

@kotlinx.serialization.Serializable
data class Edit(
    val id: String = "",
    val status: String = "",
    var eTag: String = ""
)
