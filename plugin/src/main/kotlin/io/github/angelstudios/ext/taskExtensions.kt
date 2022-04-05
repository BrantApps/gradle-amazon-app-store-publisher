package io.github.angelstudios.ext

import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Task

internal val Task.androidComponents: AndroidComponentsExtension<*, *, *>
    get() = project.androidComponents
