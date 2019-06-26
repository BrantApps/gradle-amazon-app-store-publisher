package app.brant.amazonappstorepublisher.edits

import app.brant.amazonappstorepublisher.PublishPlugin
import app.brant.amazonappstorepublisher.fetchtoken.Token
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

@kotlinx.serialization.Serializable
data class Edit(val id: String = "", val status: String = "", var eTag: String = "")

class EditsService(val token: Token, val version: String, val applicationId: String) {
    interface GetActiveEdit {
        @GET("{version}/applications/{appId}/edits")
        fun getActiveEdit(
                @Header("Authorization") authorization: String,
                @Path("version") version: String,
                @Path("appId") applicationId: String
        ): Call<Edit?>
    }

    interface CreateEdit {
        @POST("{version}/applications/{appId}/edits")
        fun createEdit(
                @Header("Authorization") authorization: String,
                @Path("version") version: String,
                @Path("appId") applicationId: String
        ): Call<Edit>
    }

    interface DeleteEdit {
        @DELETE("{version}/applications/{appId}/edits/{editId}")
        fun deleteEdit(
                @Header("Authorization") authorization: String,
                @Header("If-Match") eTag: String,
                @Path("version") version: String,
                @Path("appId") applicationId: String,
                @Path("editId") editId: String
        ): Call<ResponseBody>
    }

    fun getActiveEdit(): Edit? {
        val activeEdit = PublishPlugin.retrofit
                .create(EditsService.GetActiveEdit::class.java)
        val response = activeEdit.getActiveEdit(
                "Bearer ${token.access_token}",
                version,
                applicationId).execute()
        return extractEditData(response)
    }

    fun createEdit(): Edit? {
        val editsService = PublishPlugin.retrofit
                .create(EditsService.CreateEdit::class.java)
        val response: Response<Edit?> = editsService.createEdit(
                "Bearer ${token.access_token}",
                version,
                applicationId
        ).execute()
        return extractEditData(response)
    }

    fun deleteEdit(edit: Edit): Boolean {
        val editsService = PublishPlugin.retrofit
                .create(EditsService.DeleteEdit::class.java)
        val response: Response<ResponseBody> = editsService.deleteEdit(
                "Bearer ${token.access_token}",
                edit.eTag,
                version,
                applicationId,
                edit.id).execute()
        return response.isSuccessful
    }


    private fun extractEditData(response: Response<Edit?>): Edit? {
        val activeEdit = response.body()
        if (activeEdit != null && response.headers().get("ETag") != null) {
            activeEdit.eTag = response.headers().get("ETag")!!
        }
        return activeEdit
    }
}