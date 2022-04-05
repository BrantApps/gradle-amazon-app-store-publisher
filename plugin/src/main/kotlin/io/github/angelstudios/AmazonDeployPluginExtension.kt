package io.github.angelstudios

import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import java.io.File

interface AmazonDeployPluginExtension {

    @get:Input
    val applicationFlavor: Property<String>

    @get:Input
    val applicationId: Property<String>

    @get:Input
    val replaceApks: Property<Boolean>

    @get:Input
    val replaceEdit: Property<Boolean>

    @get:Input
    val securityProfile: Property<File>

    @get:Input
    val useOnlyUniversalApk: Property<Boolean>

}
