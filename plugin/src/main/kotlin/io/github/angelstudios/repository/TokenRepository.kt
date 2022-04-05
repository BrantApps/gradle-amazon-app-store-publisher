package io.github.angelstudios.repository

import kotlinx.serialization.json.Json
import io.github.angelstudios.api.GetToken
import io.github.angelstudios.api.model.SecurityProfile
import io.github.angelstudios.api.model.Token
import retrofit2.Response
import retrofit2.Retrofit
import java.io.File

class TokenRepository(
    private val retrofit: Retrofit
) {

    private val tokenService by lazy { retrofit.create(GetToken::class.java) }

    fun fetchToken(securityProfileFile: File): Token {
        val parsedProfile = parseSecurityProfile(securityProfileFile)
        val response: Response<Token> = tokenService.fetchToken(
            parsedProfile.grant_type,
            parsedProfile.client_id,
            parsedProfile.client_secret,
            parsedProfile.scope
        ).execute()
        return response.body()!!
    }

    private fun parseSecurityProfile(securityProfileFile: File): SecurityProfile =
        Json.decodeFromString(SecurityProfile.serializer(), securityProfileFile.readText())
}
