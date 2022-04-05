package io.github.angelstudios.api

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface AttachApk {
    @POST("{version}/applications/{appId}/edits/{editId}/apks/attach")
    fun attachApkToEdit(
        @Header("Authorization") authorization: String,
        @Path("version") version: String,
        @Path("appId") applicationId: String,
        @Path("editId") editId: String,
        @Body apkAsset: RequestBody
    ): Call<ResponseBody>
}
