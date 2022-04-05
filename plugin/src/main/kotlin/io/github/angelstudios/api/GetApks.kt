package io.github.angelstudios.api

import io.github.angelstudios.api.model.Apk
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface GetApks {
    @GET("{version}/applications/{appId}/edits/{editId}/apks")
    fun getApksForEdit(
        @Header("Authorization") authorization: String,
        @Path("version") version: String,
        @Path("appId") applicationId: String,
        @Path("editId") editId: String
    ): Call<List<Apk>>

    @GET("{version}/applications/{appId}/edits/{editId}/apks/{apkId}")
    fun getApkForEdit(
        @Header("Authorization") authorization: String,
        @Path("version") version: String,
        @Path("appId") applicationId: String,
        @Path("editId") editId: String,
        @Path("apkId") apkId: String
    ): Call<Apk>
}
