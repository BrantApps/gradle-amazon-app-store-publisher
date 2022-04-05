package io.github.angelstudios.api.model

@kotlinx.serialization.Serializable
data class Apk(
    val versionCode: Int,
    val id: String,
    val name: String,
    val eTag: String = ""
)
