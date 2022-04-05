package io.github.angelstudios.api

import io.github.angelstudios.api.model.Token
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

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
