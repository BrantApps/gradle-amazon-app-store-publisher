package io.github.angelstudios.api

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PUT
import retrofit2.http.Path

interface ReplaceApk {
    @Headers("Content-Type: application/vnd.android.package-archive")
    @PUT("{version}/applications/{appId}/edits/{editId}/apks/{apkId}/replace")
    fun replaceApk(
        @Header("Authorization") authorization: String,
        @Header("If-Match") eTag: String,
        @Header("filename") filename: String?,
        @Path("version") version: String,
        @Path("appId") applicationId: String,
        @Path("editId") editId: String,
        @Path("apkId") apkId: String,
        @Body file: RequestBody
    ): Call<ResponseBody>
}
