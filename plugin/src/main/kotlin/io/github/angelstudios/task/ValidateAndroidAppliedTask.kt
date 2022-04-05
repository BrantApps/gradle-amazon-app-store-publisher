package io.github.angelstudios.task

import com.android.build.gradle.AppPlugin
import org.gradle.api.DefaultTask
import org.gradle.api.plugins.UnknownPluginException
import org.gradle.api.tasks.TaskAction
import io.github.angelstudios.exception.ConfigurationException

open class ValidateAndroidAppliedTask : DefaultTask() {

    init {
        group = "verification"
        description = "Verify the Android App Plugin was applied prior to the Amazon Deploy plugin"
    }

    @TaskAction
    fun run() {
        try {
            project.plugins.getPlugin(AppPlugin::class.java)
        } catch (_: UnknownPluginException) {
            throw ConfigurationException("Android plugin not detected. Must be applied first.")
        }
    }

}
