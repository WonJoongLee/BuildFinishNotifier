plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.8.10"
    id("org.jetbrains.intellij") version "1.13.3"
}

group = "com.isaac"
version = "1.0.4"

repositories {
    mavenCentral()
}

intellij {
    plugins.set(listOf("Kotlin", "android"))
    localPath.set("/Applications/Android Studio.app/Contents")
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
        untilBuild.set("233.*")
        changeNotes.set("It now supports the Iguana version of Android Studio!")
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
