package com.brantapps.amazonapkpublisher.fetchtoken

import com.brantapps.amazonapkpublisher.AmazonAppPublishPlugin
import kotlinx.serialization.json.Json
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import java.io.File

@kotlinx.serialization.Serializable
data class SecurityProfile(val grant_type: String, val client_id: String, val client_secret: String, val scope: String)

@kotlinx.serialization.Serializable
data class Token(val access_token: String, val scope: String, val token_type: String, val expires_in: String)

class FetchTokenService {
    interface GetToken {
        @FormUrlEncoded
        @POST("https://api.amazon.com/auth/o2/token")
        fun fetchToken(
                @Field("grant_type") clientCredentials: String,
                @Field("client_id") clientId: String,
                @Field("client_secret") clientSecret: String,
                @Field("scope") scope: String
        ): Call<Token>
    }

    fun fetchToken(securityProfileFile: File?): Token? {
        val parsedProfile = parseSecurityProfile(securityProfileFile)
        val tokenService = AmazonAppPublishPlugin.retrofit
                .create(FetchTokenService.GetToken::class.java)
        val response: Response<Token> = tokenService.fetchToken(
                parsedProfile.grant_type,
                parsedProfile.client_id,
                parsedProfile.client_secret,
                parsedProfile.scope).execute()
        return response.body()
    }

    fun parseSecurityProfile(securityProfileFile: File?): SecurityProfile {
        return Json.parse(SecurityProfile.serializer(), securityProfileFile!!.readText())
    }
}