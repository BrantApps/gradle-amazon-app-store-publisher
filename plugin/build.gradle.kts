plugins {
    id("com.gradle.plugin-publish") version "0.10.1"
    id("org.gradle.kotlin.kotlin-dsl") version "1.2.8"
    id("kotlinx-serialization") version "1.3.31"
    `maven-publish`
}

group = "app.brant"
version = "0.1.1"

val pluginArtifactId = "amazonappstorepublisher"
val pluginVcsUrl = "https://github.com/BrantApps/gradle-amazon-app-store-publisher"

gradlePlugin {
    plugins {
        register("AmazonAppStorePublisher") {
            id = project.group as String + "." + pluginArtifactId
            displayName = "Gradle Amazon App Store Publisher"
            description = "Gradle Amazon App Store Publisher allows you to upload your APKs to the amazon app store"
            implementationClass = "app.brant.amazonappstorepublisher.PublishPlugin"
        }
    }
}

val sourcesJar by tasks.registering(Jar::class) {
    classifier = "sources"
    from(sourceSets["main"].allSource)
    dependsOn(tasks["classes"])
}

afterEvaluate {
    publishing.publications.named<MavenPublication>("pluginMaven") {
        artifactId = pluginArtifactId
        artifact(sourcesJar.get())

        pom {
            name.set("Amazon App Store Publisher")
            description.set("Gradle Amazon App Store Publisher allows you" +
                    "to upload your APKs to the amazon app store")
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

pluginBundle {
    website = pluginVcsUrl
    vcsUrl = pluginVcsUrl
    tags = listOf("android", "amazon-app-store", "submission-api")

    mavenCoordinates {
        groupId = project.group as String
        artifactId = pluginArtifactId
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

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}

repositories {
    google()
    jcenter()
}

dependencies {
    implementation("com.android.tools.build:gradle:3.4.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.11.0")
    implementation("com.squareup.retrofit2:retrofit:2.5.0")
    implementation("com.squareup.okhttp3:logging-interceptor:3.10.0")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.4.0")
}