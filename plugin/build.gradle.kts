plugins {
    id("com.gradle.plugin-publish") version "1.2.0"
    id("org.jetbrains.kotlin.jvm") version "1.8.20"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.20"
    `kotlin-dsl`
}

group = "app.brant"
version = "0.1.1"

val pluginArtifactId = "amazonappstorepublisher"
val pluginVcsUrl = "https://github.com/BrantApps/gradle-amazon-app-store-publisher"

gradlePlugin {
    website.set(pluginVcsUrl)
    vcsUrl.set(pluginVcsUrl)
    plugins {
        register("AmazonAppStorePublisher") {
            id = project.group as String + "." + pluginArtifactId
            displayName = "Gradle Amazon App Store Publisher"
            description = "Gradle Amazon App Store Publisher allows you to upload your APKs to the amazon app store"
            implementationClass = "app.brant.amazonappstorepublisher.PublishPlugin"
            tags.set(listOf("android", "amazon-app-store", "submission-api"))
        }
    }
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
    dependsOn(tasks["classes"])
}

afterEvaluate {
    publishing.publications.named<MavenPublication>("pluginMaven") {
        artifactId = pluginArtifactId
        artifact(sourcesJar.get())

        pom {
            name.set("Amazon App Store Publisher")
            description.set(
                "Gradle Amazon App Store Publisher allows you" +
                        "to upload your APKs to the amazon app store"
            )
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
                    id.set("brantapps")
                    name.set("David Branton")
                    email.set("oceanlife.development@gmail.com")
                }
            }
        }
    }
}

publishing {
    repositories {
        maven {
            name = "Snapshots"
            url = uri("https://oss.sonatype.org/content/repositories/snapshots/")

            credentials {
                username = System.getenv("SONATYPE_NEXUS_USERNAME")
                password = System.getenv("SONATYPE_NEXUS_PASSWORD")
            }
        }
    }
}

repositories {
    google()
    mavenCentral()
}

configurations.all {
    resolutionStrategy {
        eachDependency {
            if (requested.group == "com.google.protobuf" && requested.name == "protobuf-java") {
                val requestedVersion = requested.version
                if (requestedVersion == null) {
                    useVersion("3.23.4")
                } else {
                    val (major, minor, patch) = requestedVersion.split(".")
                    if (major == "3" && minor == "19" && patch.toInt() < 6) {
                        useVersion("3.23.4") // just use the latest version
                    }
                }
            }
        }
    }
}

dependencies {
    implementation("com.android.tools.build:gradle-api:8.0.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")
}