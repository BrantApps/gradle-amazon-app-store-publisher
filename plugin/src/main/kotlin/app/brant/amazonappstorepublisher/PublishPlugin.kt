package app.brant.amazonappstorepublisher

import app.brant.amazonappstorepublisher.apks.ApkService
import app.brant.amazonappstorepublisher.edits.Edit
import app.brant.amazonappstorepublisher.edits.EditsService
import app.brant.amazonappstorepublisher.fetchtoken.FetchTokenService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.invoke
import retrofit2.Retrofit
import java.io.File
import java.util.concurrent.TimeUnit
import javax.naming.ConfigurationException

@Suppress("unused") // Used by Gradle
class PublishPlugin : Plugin<Project> {
    companion object {
        private val contentType: MediaType = "application/json".toMediaType()
        const val apiVersion = "v1"
        const val pluginDslRoot = "amazon"
        val json: Json = Json { isLenient = true }
        val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl("https://developer.amazon.com/api/appstore/")
                .addConverterFactory(
                        json.asConverterFactory(contentType))
                .client(buildOkHttpClient())
                .build()

        private fun buildOkHttpClient(): OkHttpClient {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            val httpClient = OkHttpClient.Builder()
            httpClient.addInterceptor(logging)
            httpClient.writeTimeout(System.getProperty("${PublishPlugin::class.qualifiedName}.writeTimeout", "60").toLong(), TimeUnit.SECONDS)
            httpClient.readTimeout(System.getProperty("${PublishPlugin::class.qualifiedName}.readTimeout", "30").toLong(), TimeUnit.SECONDS)
            return httpClient.build()
        }
    }

    override fun apply(project: Project): Unit = project.run {
        val amazon = extensions.create(
                pluginDslRoot,
                PublishPluginExtension::class.java
        )

        tasks {
            register("publishToAmazonAppStore") {
                group = "publishing"
                description = "Uploads APKs to the Amazon App Store."
                doLast {
                    validateInputs(amazon)
                    val securityProfile = amazon.securityProfile!!
                    val applicationId = amazon.applicationId!!
                    println("️\uD83D\uDD12 Authenticating...")
                    val token = FetchTokenService().fetchToken(securityProfile)!!

                    val editsService = EditsService(token, apiVersion, applicationId)
                    val apkService = ApkService(token, apiVersion, applicationId)

                    val activeEdit = editsService.getActiveEdit()

                    if (amazon.replaceEdit) {
                        println("️↕️️ Deleting edit...")
                        if (activeEdit != null &&
                                activeEdit.id.isNotBlank()) {
                            editsService.deleteEdit(activeEdit)
                        }
                    }

                    var newEdit = editsService.getActiveEdit()
                    if (newEdit == null ||
                            newEdit.id.isBlank()) {
                        println("️\uD83C\uDD95️ Creating new edit...")
                        newEdit = editsService.createEdit()
                    }

                    if (amazon.replaceApks) {
                        replaceExistingApksOnEdit(apkService, newEdit!!, amazon.pathToApks)
                    }
                    else {
                        deleteExistingApksOnEdit(apkService, newEdit!!)
                        uploadApksAndAttachToEdit(
                                apkService,
                                newEdit,
                                amazon.pathToApks
                        )
                    }
                }
            }
        }
    }

    private fun validateInputs(amazon: PublishPluginExtension) {
        if (amazon.securityProfile == null) {
            throw ConfigurationException("Missing required path to LWA security profile")
        }

        if (amazon.applicationId == null) {
            throw ConfigurationException("Specify your apps application identifier")
        }

        if (amazon.pathToApks.isEmpty()) {
            throw ConfigurationException("No APKs to upload")
        }
    }

    private fun replaceExistingApksOnEdit(
            apkService: ApkService,
            activeEdit: Edit,
            apksToReplace: List<File>) {
        val apks = apkService.getApks(activeEdit.id)
        if (apks.size != apksToReplace.size) {
            throw IllegalStateException("❌ Number of existing APKs on edit (${apks.size}) does not match" +
                "the number of APKs to upload (${apksToReplace.size})")
        }
        println("\ud83d\udd04 Replacing APKs in existing edit...")
        apksToReplace.forEachIndexed { index, apkFile ->
            println("\u23eb Uploading ${apkFile}...")
            val status = apkService.replaceApk(activeEdit.id, apks[index].id, apkFile, apkFile.getName())
            if (!status) {
                println("❌ Failed to upload APK")
                throw IllegalStateException("Failed to upload APK")
            }
        }
        println("\uD83C\uDF89 New APK(s) published to the Amazon App Store")
    }

    private fun deleteExistingApksOnEdit(
            apkService: ApkService,
            activeEdit: Edit) {
        val apks = apkService.getApks(activeEdit.id)
        println("⬅️ Remove APKs from previous edit...")
        apks.forEach {
            val status = apkService.deleteApk(activeEdit.id, it.id)
            if (!status) {
                throw IllegalStateException("❌ Failed to delete existing APK")
            }
        }
    }

    private fun uploadApksAndAttachToEdit(
            apkService: ApkService,
            activeEdit: Edit,
            apksToUpload: List<File>) {
        apksToUpload.forEachIndexed { index, apk ->
            println("⏫ Uploading new APK(s)...")
            val result = apkService.uploadApk(activeEdit.id, apk, "APK-$index")
            if (result) {
                println("\uD83C\uDF89 New APK(s) published to the Amazon App Store...")
            } else {
                println("❌ Failed to upload new APK(s)...")
                throw IllegalStateException("Failed to upload new APK(s)...")
            }
        }
    }
}