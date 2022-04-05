package io.github.angelstudios.api

import io.github.angelstudios.api.model.Edit
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface GetActiveEdit {
    @GET("{version}/applications/{appId}/edits")
    fun getActiveEdit(
        @Header("Authorization") authorization: String,
        @Path("version") version: String,
        @Path("appId") applicationId: String
    ): Call<Edit?>
}
