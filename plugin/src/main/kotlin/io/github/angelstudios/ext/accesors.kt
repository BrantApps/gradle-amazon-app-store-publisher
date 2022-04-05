package io.github.angelstudios.ext

import org.gradle.api.Action
import io.github.angelstudios.AmazonDeployPluginExtension

/**
 * Extension function to simplify creation of the Amazon Deploy configuration object.
 */
fun org.gradle.api.Project.amazonDeploy(configure: Action<AmazonDeployPluginExtension>): Unit =
    (this as org.gradle.api.plugins.ExtensionAware).extensions.configure("amazon", configure)
