package io.github.angelstudios

import com.android.build.api.extension.impl.VariantSelectorImpl
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.gradle.api.Plugin
import org.gradle.api.Project
import io.github.angelstudios.ext.androidComponents
import io.github.angelstudios.task.UploadToAmazonAppStoreTask
import io.github.angelstudios.task.ValidateAmazonExtensionTask
import io.github.angelstudios.task.ValidateAndroidAppliedTask
import retrofit2.Retrofit
import java.util.Locale
import java.util.concurrent.TimeUnit

@ExperimentalSerializationApi
class AmazonDeployPlugin : Plugin<Project> {

    private val retrofitInstance: Retrofit = Retrofit.Builder()
        .baseUrl("https://developer.amazon.com/api/appstore/")
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .client(buildOkHttpClient())
        .build()

    private fun buildOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder().apply {
            addInterceptor(logging)
            writeTimeout(60, TimeUnit.SECONDS)
            readTimeout(30, TimeUnit.SECONDS)
        }.build()
    }

    override fun apply(target: Project): Unit = target.run {
        val amazonExt = extensions.create(
            "amazon",
            AmazonDeployPluginExtension::class.java
        )

        if (!amazonExt.applicationFlavor.isPresent) amazonExt.applicationFlavor.set("release")
        amazonExt.applicationFlavor.convention("release")
        amazonExt.replaceApks.convention(false)
        amazonExt.replaceEdit.convention(false)
        amazonExt.useOnlyUniversalApk.convention(true)

        val validateAndroidApplied = tasks.register(
            "validateAndroidAppliedTask",
            ValidateAndroidAppliedTask::class.java
        )

        val validateAmazonExtensionTask = tasks.register(
            "validateAmazonExtension",
            ValidateAmazonExtensionTask::class.java
        ) {
            amazon.set(amazonExt)
            dependsOn(validateAndroidApplied)
        }

        val variantSelector = VariantSelectorImpl().withName(amazonExt.applicationFlavor.get())
        target.androidComponents.onVariants(variantSelector) { variantInstance ->
            tasks.register("publishToAmazonAppStore", UploadToAmazonAppStoreTask::class.java) {
                amazon.set(amazonExt)
                retrofit.set(retrofitInstance)
                variant.set(variantInstance)
                dependsOn(
                    validateAmazonExtensionTask,
                    "assemble${amazonExt.applicationFlavor.get().capitalize(Locale.ENGLISH)}"
                )
            }
        }
    }

}
