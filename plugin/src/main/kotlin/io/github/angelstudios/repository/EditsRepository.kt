package io.github.angelstudios.repository

import okhttp3.ResponseBody
import io.github.angelstudios.api.CreateEdit
import io.github.angelstudios.api.DeleteEdit
import io.github.angelstudios.api.GetActiveEdit
import io.github.angelstudios.api.model.Edit
import io.github.angelstudios.api.model.Token
import retrofit2.Response
import retrofit2.Retrofit

class EditsRepository(
    private val retrofit: Retrofit,
    private val token: Token,
    private val applicationId: String
) {

    fun getActiveEdit(): Edit? {
        val activeEdit = retrofit.create(GetActiveEdit::class.java)
        val response = activeEdit.getActiveEdit(
            "Bearer ${token.access_token}",
            AMAZON_APP_STORE_API_VERSION,
            applicationId
        ).execute()
        return extractEditData(response)
    }

    fun createEdit(): Edit {
        val editsService = retrofit.create(CreateEdit::class.java)
        val response: Response<Edit?> = editsService.createEdit(
            "Bearer ${token.access_token}",
            AMAZON_APP_STORE_API_VERSION,
            applicationId
        ).execute()
        return extractEditData(response) ?: error("Failed to create new Edit:")
    }

    fun deleteEdit(edit: Edit): Boolean {
        val editsService = retrofit.create(DeleteEdit::class.java)
        val response: Response<ResponseBody> = editsService.deleteEdit(
            "Bearer ${token.access_token}",
            edit.eTag,
            AMAZON_APP_STORE_API_VERSION,
            applicationId,
            edit.id
        ).execute()
        return response.isSuccessful
    }

    private fun extractEditData(response: Response<Edit?>): Edit? {
        val activeEdit = response.body()
        if (activeEdit != null && response.headers()["ETag"] != null) {
            activeEdit.eTag = response.headers()["ETag"]!!
        }
        return activeEdit
    }
}
