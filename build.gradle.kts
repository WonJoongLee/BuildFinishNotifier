plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.23"
    id("org.jetbrains.intellij") version "1.13.3"
}

group = "com.isaac"
version = "1.0.12"

repositories {
    mavenCentral()
}

intellij {
    plugins.set(listOf("Kotlin", "android"))
    version.set("2023.2.2")
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }

    patchPluginXml {
        sinceBuild.set("213")
        // No upper bound: a new Android Studio release should not require a plugin release.
        // Note that simply omitting this line does NOT work - gradle-intellij-plugin then
        // falls back to a convention derived from `intellij.version` (232.* here).
        untilBuild.set(provider { null })
        changeNotes.set("Removed the upper IDE build limit, so new Android Studio releases no longer require a plugin update.")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
