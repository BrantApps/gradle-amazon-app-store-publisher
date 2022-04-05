package io.github.angelstudios.task

import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.BuiltArtifact
import com.android.build.api.variant.BuiltArtifacts
import com.android.build.api.variant.Variant
import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import io.github.angelstudios.AmazonDeployPluginExtension
import io.github.angelstudios.api.model.Edit
import io.github.angelstudios.ext.androidComponents
import io.github.angelstudios.repository.ApkRepository
import io.github.angelstudios.repository.EditsRepository
import io.github.angelstudios.repository.TokenRepository
import retrofit2.Retrofit
import java.io.File

abstract class UploadToAmazonAppStoreTask : DefaultTask() {

    @get:Internal
    abstract val amazon: Property<AmazonDeployPluginExtension>

    @get:Internal
    abstract val retrofit: Property<Retrofit>

    @get:Internal
    abstract val variant: Property<Variant>

    private val android: AndroidComponentsExtension<*, *, *> by lazy {
        androidComponents
    }

    init {
        group = "publishing"
        description = "Uploads APKs to the Amazon App Store."
    }

    @TaskAction
    fun run() {
        val amazonExt = amazon.get()
        val applicationId = amazonExt.applicationId.get()
        val retrofitInstance = retrofit.get()
        val token = TokenRepository(retrofitInstance).fetchToken(amazonExt.securityProfile.get())
        val apkRepository = ApkRepository(
            retrofit = retrofitInstance,
            token = token,
            applicationId = applicationId
        )

        val editsRepository = EditsRepository(
            retrofitInstance,
            token,
            applicationId = applicationId
        )

        if (amazonExt.replaceEdit.get()) {
            logger.info("Checking for active Edit to delete")
            val activeEdit = editsRepository.getActiveEdit()
            if (activeEdit != null && activeEdit.id.isNotBlank()) {
                editsRepository.deleteEdit(activeEdit)
            }
        }

        val edit = editsRepository.getActiveEdit().let { activeEdit ->
            if (activeEdit == null || activeEdit.id.isBlank()) {
                logger.info("Creating new edit")
                editsRepository.createEdit()
            } else activeEdit
        }

        if (amazonExt.replaceApks.get()) {
            replaceExistingApksOnEdit(apkRepository, edit, getApkPaths(amazonExt.useOnlyUniversalApk.get()))
        } else {
            deleteExistingApksOnEdit(apkRepository, edit)
            uploadApksAndAttachToEdit(
                apkRepository,
                edit,
                getApkPaths(amazonExt.useOnlyUniversalApk.get())
            )
        }
    }

    private fun getApkPaths(useOnlyUniversalApk: Boolean): List<File> {
        val variant = variant.get()
        val files = mutableListOf<File>()
        val apksPath: Directory = variant.artifacts.get(SingleArtifact.APK).get()
        val builtArtifacts: BuiltArtifacts = variant.artifacts.getBuiltArtifactsLoader().load(apksPath)
            ?: error("Cannot load APKs")

        builtArtifacts.elements
            .asSequence()
            .filter { artifact ->
                if (useOnlyUniversalApk) artifact.outputFile.endsWith("universal-${variant.name}.apk")
                else true
            }
            .map(BuiltArtifact::outputFile)
            .map(::File)
            .toCollection(files)

        return files
    }

    private fun replaceExistingApksOnEdit(
        apkService: ApkRepository,
        activeEdit: Edit,
        apksToReplace: List<File>
    ) {
        val apks = apkService.getApks(activeEdit.id)
        if (apks.size != apksToReplace.size) {
            throw IllegalStateException(
                "$UNICODE_ERROR Number of existing APKs on edit (${apks.size}) does not match" +
                    "the number of APKs to upload (${apksToReplace.size})"
            )
        }
        logger.quiet("$UNICODE_REPEAT Replacing APKs in existing edit...")
        logger.quiet("$UNICODE_UPLOAD Preparing to upload apks\n[${apksToReplace.joinToString()}]")
        apksToReplace.forEachIndexed { index, apkFile ->
            logger.quiet("$UNICODE_UPLOAD Uploading ${apkFile}...")
            val status = apkService.replaceApk(activeEdit.id, apks[index].id, apkFile, apkFile.name)
            if (!status) throw IllegalStateException("$UNICODE_ERROR Failed to upload APK")
        }
        logger.quiet("$UNICODE_PARTY New APK(s) published to the Amazon App Store")
    }

    private fun deleteExistingApksOnEdit(
        apkService: ApkRepository,
        activeEdit: Edit
    ) {
        val apks = apkService.getApks(activeEdit.id)
        logger.quiet("$UNICODE_DELETE Remove APKs from previous edit...")
        apks.forEach {
            val status = apkService.deleteApk(activeEdit.id, it.id)
            if (!status) throw IllegalStateException("$UNICODE_ERROR Failed to delete existing APK")
        }
    }

    private fun uploadApksAndAttachToEdit(
        apkService: ApkRepository,
        activeEdit: Edit,
        apksToUpload: List<File>
    ) {
        logger.quiet("$UNICODE_UPLOAD Preparing to upload apks\n[${apksToUpload.joinToString()}]")
        apksToUpload.forEachIndexed { index, apk ->
            logger.quiet("$UNICODE_UPLOAD Uploading new APK(s)...")
            val result = apkService.uploadApk(activeEdit.id, apk, "APK-$index")
            if (result) logger.quiet("$UNICODE_PARTY New APK(s) published to the Amazon App Store...")
            else throw IllegalStateException("$UNICODE_ERROR Failed to upload new APK(s)...")
        }
    }

    companion object {
        private const val UNICODE_PARTY = "\uD83C\uDF89"
        private const val UNICODE_REPEAT = "\uD83D\uDD04"
        private const val UNICODE_UPLOAD = "\u23eb"
        private const val UNICODE_ERROR = "\ud83d\uded1"
        private const val UNICODE_DELETE = "\ud83d\uddd1"
    }

}
