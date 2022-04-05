package io.github.angelstudios.api

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface UploadApk {
    @Headers("Content-Type: application/vnd.android.package-archive")
    @POST("{version}/applications/{appId}/edits/{editId}/apks/upload")
    fun uploadApk(
        @Header("Authorization") authorization: String,
        @Header("filename") filename: String?,
        @Path("version") version: String,
        @Path("appId") applicationId: String,
        @Path("editId") editId: String,
        @Body file: RequestBody
    ): Call<ResponseBody>
}
