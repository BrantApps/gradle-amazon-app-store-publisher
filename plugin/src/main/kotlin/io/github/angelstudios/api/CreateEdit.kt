package io.github.angelstudios.api

import io.github.angelstudios.api.model.Edit
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface CreateEdit {
    @POST("{version}/applications/{appId}/edits")
    fun createEdit(
        @Header("Authorization") authorization: String,
        @Path("version") version: String,
        @Path("appId") applicationId: String
    ): Call<Edit>
}
