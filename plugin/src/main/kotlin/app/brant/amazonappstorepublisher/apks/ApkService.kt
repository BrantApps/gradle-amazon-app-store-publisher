package app.brant.amazonappstorepublisher.apks

import app.brant.amazonappstorepublisher.PublishPlugin
import app.brant.amazonappstorepublisher.edits.Edit
import app.brant.amazonappstorepublisher.fetchtoken.Token
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import java.io.File

@kotlinx.serialization.Serializable
data class Apk(val versionCode: Int, val id: String, val name: String, val eTag: String = "")

@kotlinx.serialization.Serializable
data class ApkAssetResource(val fieldIs: String)

class ApkService(val token: Token,
                 val version: String,
                 val applicationId: String) {
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

    interface DeleteApk {
        @DELETE("{version}/applications/{appId}/edits/{editId}/apks/{apkId}")
        fun deleteApk(
                @Header("Authorization") authorization: String,
                @Header("If-Match") eTag: String,
                @Path("version") version: String,
                @Path("appId") applicationId: String,
                @Path("editId") editId: String,
                @Path("apkId") apkId: String
        ): Call<ResponseBody>
    }

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


    fun getApk(editId: String, apkId: String): Apk {
        val getApksService = PublishPlugin.retrofit
                .create(ApkService.GetApks::class.java)
        val response: Response<Apk> = getApksService.getApkForEdit(
                "Bearer ${token.access_token}",
                version,
                applicationId,
                editId,
                apkId
        ).execute()
        val apk = response.body()!!
        val eTag = response.headers().get("ETag")!!
        return Apk(apk.versionCode, apk.id, apk.name, eTag)
    }

    fun getApks(editId: String): List<Apk> {
        val getApksService = PublishPlugin.retrofit
                .create(ApkService.GetApks::class.java)
        val response: Response<List<Apk>> = getApksService.getApksForEdit(
                "Bearer ${token.access_token}",
                version,
                applicationId,
                editId
        ).execute()
        return extractApkData(response)
    }

    fun deleteApk(editId: String, apkId: String): Boolean {
        val apk = getApk(editId, apkId)
        val deleteApkService = PublishPlugin.retrofit
                .create(ApkService.DeleteApk::class.java)
        val response: Response<ResponseBody> =
                deleteApkService.deleteApk(
                        "Bearer ${token.access_token}",
                        apk.eTag,
                        version,
                        applicationId,
                        editId,
                        apkId
                ).execute()
        return response.isSuccessful
    }

    fun replaceApk(editId: String, apkId: String, apkFile: File, filename: String?): Boolean {
        val apk = getApk(editId, apkId)
        val replaceApkService = PublishPlugin.retrofit
                .create(ApkService.ReplaceApk::class.java)
        val requestBody = apkFile.readBytes().toRequestBody(
                "application/vnd.android.package-archive".toMediaType()
        )

        val response: Response<ResponseBody> =
                replaceApkService.replaceApk(
                        "Bearer ${token.access_token}",
                        apk.eTag,
                        filename,
                        version,
                        applicationId,
                        editId,
                        apkId,
                        requestBody
                ).execute()
        return response.isSuccessful
    }

    fun uploadApk(editId: String, apk: File, filename: String?): Boolean {
        val uploadApkService = PublishPlugin.retrofit
                .create(ApkService.UploadApk::class.java)
        val requestBody = apk.readBytes().toRequestBody(
                "application/octet-stream".toMediaType()
        )

        val response: Response<ResponseBody> =
                uploadApkService.uploadApk(
                        "Bearer ${token.access_token}",
                        filename,
                        version,
                        applicationId,
                        editId,
                        requestBody
                ).execute()
        return response.isSuccessful
    }

    fun attachApkToEdit(editId: Edit, apkAssetResource: ApkAssetResource): Boolean {
        val attachApkService = PublishPlugin.retrofit
                .create(ApkService.AttachApk::class.java)
        val requestBody = Json.encodeToString(ApkAssetResource.serializer(), apkAssetResource)
            .toRequestBody("application/json".toMediaType())

        val response: Response<ResponseBody> =
                attachApkService.attachApkToEdit(
                        "Bearer ${token.access_token}",
                        version,
                        applicationId,
                        editId.id,
                        requestBody
                ).execute()
        return response.isSuccessful
    }


    private fun extractApkData(response: Response<List<Apk>>): List<Apk> {
        val list = response.body()
        return list!!.map {
            Apk(it.versionCode, it.id, it.name)
        }
    }
}