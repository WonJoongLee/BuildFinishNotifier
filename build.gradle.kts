plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.8.10"
    id("org.jetbrains.intellij") version "1.13.3"
}

group = "com.isaac"
version = "1.0.2"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.googlecode.soundlibs:jlayer:1.0.1.4")
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
        untilBuild.set("231.*")
        changeNotes.set("This plugin is designed to notify you with sound effects when your build process succeeds or failed.")
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
