package io.github.angelstudios.task

import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import io.github.angelstudios.AmazonDeployPluginExtension
import io.github.angelstudios.exception.ConfigurationException
import io.github.angelstudios.ext.androidComponents

abstract class ValidateAmazonExtensionTask : DefaultTask() {

    @get:Internal
    abstract val amazon: Property<AmazonDeployPluginExtension>

    private val android: AndroidComponentsExtension<*, *, *> by lazy { androidComponents }

    init {
        group = "verification"
        description = "Verify the configuration of the Amazon Deploy plugin"
    }

    @TaskAction
    fun run() {
        val amazonExt = amazon.get()
        if (!amazonExt.securityProfile.get().exists())
            throw ConfigurationException(
                "Security Profile missing at ${amazonExt.securityProfile.get().path}")

        if (amazonExt.applicationFlavor.get().isBlank())
            throw ConfigurationException("Application variant must not be blank")

        if (amazonExt.applicationId.get().isBlank())
            throw ConfigurationException("Application ID must not be null or blank")

        android.finalizeDsl { ext ->
            try {
                ext.productFlavors.first { it.name == amazonExt.applicationFlavor.get() }
            } catch (_: NoSuchElementException) {
                throw ConfigurationException(
                    "Application Variant ${amazonExt.applicationFlavor} does not exist")
            }
        }
    }

}
