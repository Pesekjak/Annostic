plugins {
    id("annostic.conventions-library")
    `kotlin-dsl`
    `java-gradle-plugin`
    alias(libs.plugins.gradle.plugin.publish)
    alias(libs.plugins.johnrengelman.shadow)
}

group = "me.pesekjak.annostic"
version = "1.0"

repositories {
    maven {
        url = uri("https://plugins.gradle.org/m2/")
    }
}

dependencies {
    implementation(libs.jetbrains.kotlin.gradle)
    implementation(libs.google.gson)
    implementation(libs.asm.asm)
    implementation(libs.asm.commons)
}

gradlePlugin {
    website.set("https://github.com/Pesekjak/Annostic")
    vcsUrl.set("https://github.com/Pesekjak/Annostic.git")

    plugins {
        create("Annostic") {
            id = "me.pesekjak.annostic-plugin"
            displayName = "Annostic"
            description = "Annostic introduces enhancement to annotation capabilities by introducing static methods within annotations."
            tags.set(listOf("annotations"))
            implementationClass = "me.pesekjak.annostic.AnnosticPlugin"
        }
    }
}

tasks {
    shadowJar {
        mergeServiceFiles()
        archiveClassifier.set("")
    }
}