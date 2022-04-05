@file:Suppress("UnstableApiUsage")

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
    `maven-publish`
    kotlin("plugin.serialization") version "1.6.10"
    id("com.gradle.plugin-publish") version "1.0.0-rc-1"
}

val catalogs = extensions.getByType<VersionCatalogsExtension>()

group = "io.github.angel-studios"
version = "1.0.0"

val pluginDisplayName = "Amazon App Store Deploy Plugin"
val pluginDescription = "Plugin for delivering APK artifacts to the Amazon App Store as new Edits."
val pluginArtifactId = "amazon-app-store-publisher"
val pluginVcsUrl = "https://github.com/Angel-Studios/gradle-amazon-app-store-publisher"

gradlePlugin {
    plugins {
        create(pluginArtifactId) {
            id = "$group.$pluginArtifactId"
            displayName = pluginDisplayName
            description = pluginDescription
            implementationClass = "io.github.angelstudios.AmazonDeployPlugin"
        }
    }
}

pluginBundle {
    website = pluginVcsUrl
    vcsUrl = pluginVcsUrl
    tags = listOf("android", "amazon-app-store", "submission-api")
    description = pluginDescription
    version = "${project.version}"
}

publishing {
    publications {
        create<MavenPublication>("pluginMaven") {
            artifactId = pluginArtifactId

            pom {
                name.set(pluginDisplayName)
                description.set(pluginDescription)
                url.set(pluginVcsUrl)

                developers {
                    developer {
                        id.set("angelstudios")
                        name.set("Angel Studios")
                    }
                }
            }
        }
    }
}

afterEvaluate {
    publishing.publications.named<MavenPublication>("pluginMaven") {
        artifactId = pluginArtifactId

        pom {
            name.set(pluginDisplayName)
            description.set(pluginDescription)
            url.set(pluginVcsUrl)
            licenses {
                license {
                    name.set("The MIT License (MIT)")
                    url.set("http://opensource.org/licenses/MIT")
                    distribution.set("repo")
                }
            }
            developers {
                developer {
                    id.set("angelstudios")
                    name.set("Angel Studios")
                }
            }
        }
    }
}

dependencies {
    val libs = catalogs.named("libs")
    implementation(libs.findDependency("gradle-android").get())
    implementation(libs.findDependency("gradle-kotlin").get())
    implementation(libs.findDependency("kotlinx-serialization-json").get())
    implementation(libs.findDependency("okhttp-logging-interceptor").get())
    implementation(libs.findDependency("retrofit").get())
    implementation(libs.findDependency("retrofit-serialization-converter").get())
}
