package io.github.angelstudios.ext

import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Project

internal val Project.androidComponents: AndroidComponentsExtension<*, *, *>
    get() = extensions.getByType(AndroidComponentsExtension::class.java)
