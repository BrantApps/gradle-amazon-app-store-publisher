package io.github.angelstudios.repository

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import io.github.angelstudios.api.AttachApk
import io.github.angelstudios.api.DeleteApk
import io.github.angelstudios.api.GetApks
import io.github.angelstudios.api.ReplaceApk
import io.github.angelstudios.api.UploadApk
import io.github.angelstudios.api.model.Apk
import io.github.angelstudios.api.model.ApkAssetResource
import io.github.angelstudios.api.model.Edit
import io.github.angelstudios.api.model.Token
import io.github.angelstudios.ext.bodyOrError
import retrofit2.Response
import retrofit2.Retrofit
import java.io.File

class ApkRepository(
    private val retrofit: Retrofit,
    private val token: Token,
    private val applicationId: String
) {

    private fun getApk(editId: String, apkId: String): Apk {
        val getApksService = retrofit.create(GetApks::class.java)
        val response: Response<Apk> = getApksService.getApkForEdit(
            "Bearer ${token.access_token}",
            AMAZON_APP_STORE_API_VERSION,
            applicationId,
            editId,
            apkId
        ).execute()
        val apk: Apk = response.bodyOrError()
        val eTag = response.headers()["ETag"]!!
        return Apk(apk.versionCode, apk.id, apk.name, eTag)
    }

    fun getApks(editId: String): List<Apk> {
        val getApksService = retrofit.create(GetApks::class.java)
        val response: Response<List<Apk>> = getApksService.getApksForEdit(
            "Bearer ${token.access_token}",
            AMAZON_APP_STORE_API_VERSION,
            applicationId,
            editId
        ).execute()
        return extractApkData(response)
    }

    fun deleteApk(editId: String, apkId: String): Boolean {
        val apk = getApk(editId, apkId)
        val deleteApkService = retrofit.create(DeleteApk::class.java)
        val response: Response<ResponseBody> = deleteApkService.deleteApk(
            "Bearer ${token.access_token}",
            apk.eTag,
            AMAZON_APP_STORE_API_VERSION,
            applicationId,
            editId,
            apkId
        ).execute()
        return response.isSuccessful
    }

    fun replaceApk(editId: String, apkId: String, apk: File, filename: String?): Boolean {
        val apkDescription = getApk(editId, apkId)
        val replaceApkService = retrofit.create(ReplaceApk::class.java)
        val apkBytes = apk.readBytes()
        val requestBody = apkBytes.toRequestBody(
            "application/vnd.android.package-archive".toMediaTypeOrNull(),
            0, apkBytes.size
        )

        val response: Response<ResponseBody> = replaceApkService.replaceApk(
            "Bearer ${token.access_token}",
            apkDescription.eTag,
            filename,
            AMAZON_APP_STORE_API_VERSION,
            applicationId,
            editId,
            apkId,
            requestBody
        ).execute()
        return response.isSuccessful
    }

    fun uploadApk(editId: String, apk: File, filename: String?): Boolean {
        val uploadApkService = retrofit.create(UploadApk::class.java)
        val apkBytes = apk.readBytes()
        val requestBody = apkBytes.toRequestBody(
            "application/vnd.android.package-archive".toMediaTypeOrNull(),
            0, apkBytes.size
        )

        val response: Response<ResponseBody> = uploadApkService.uploadApk(
            "Bearer ${token.access_token}",
            filename,
            AMAZON_APP_STORE_API_VERSION,
            applicationId,
            editId,
            requestBody
        ).execute()
        return response.isSuccessful
    }

    fun attachApkToEdit(editId: Edit, apkAssetResource: ApkAssetResource): Boolean {
        val attachApkService = retrofit.create(AttachApk::class.java)
        val requestBody = Json.encodeToString(ApkAssetResource.serializer(), apkAssetResource)
            .toRequestBody(contentType = "application/json".toMediaTypeOrNull())

        val response: Response<ResponseBody> = attachApkService.attachApkToEdit(
            "Bearer ${token.access_token}",
            AMAZON_APP_STORE_API_VERSION,
            applicationId,
            editId.id,
            requestBody
        ).execute()
        return response.isSuccessful
    }

    private fun extractApkData(response: Response<List<Apk>>): List<Apk> = response.bodyOrError()
        .map { apk -> Apk(apk.versionCode, apk.id, apk.name) }

}
