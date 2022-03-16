package app.brant.amazonappstorepublisher

import org.gradle.api.tasks.*
import java.io.File
import java.io.Serializable

@Suppress("unused") // Used by Gradle
open class PublishPluginExtension @JvmOverloads constructor(
        @get:Internal internal val name: String = "default"

) : Serializable {

    @get:Internal("Backing property for public input")
    internal var securityProfileProp: File? = null

    @get:Internal("Backing property for public input")
    internal var replaceEditProp: Boolean? = null

    @get:Internal("Backing property for public input")
    internal var replaceApksProp: Boolean? = null

    @get:Internal("Backing property for public input")
    internal var applicationIdProp: String? = null

    @get:Internal("Backing property for public input")
    internal var pathToApksProp: List<File> = mutableListOf()

    @get:PathSensitive(PathSensitivity.RELATIVE)
    @get:InputFile
    var pathToApks
        get() = pathToApksProp
        set(value) {
            pathToApksProp = value
        }

    @get:PathSensitive(PathSensitivity.RELATIVE)
    @get:InputFile
    var securityProfile
        get() = securityProfileProp
        set(value) {
            securityProfileProp = value
        }

    @get:Input
    var applicationId
        get() = applicationIdProp
        set(value) {
            applicationIdProp = value
        }

    @get:Input
    var replaceEdit
        get() = replaceEditProp ?: true
        set(value) {
            replaceEditProp = value
        }

    @get:Input
    var replaceApks
        get() = replaceApksProp ?: false
        set(value) {
            replaceApksProp = value
        }
}