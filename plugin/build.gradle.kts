plugins {
    id("org.gradle.kotlin.kotlin-dsl") version "1.2.8"
    id("kotlinx-serialization") version "1.3.31"
    `maven-publish`
}

val artifactId = "amazon-app-publish"

gradlePlugin {
    plugins {
        register("Amazon App Publish") {
            id = artifactId
            displayName = "Gradle Amazon App Store Publisher"
            description = "Gradle Amazon App Store Publisher allows you to upload your APKs to the amazon app store"
            implementationClass = "com.brantapps.amazonapkpublisher.AmazonAppPublishPlugin"
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.brantapps"
            artifactId = artifactId
            version = "1.0.0"

            from(components["java"])

            pom {
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
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