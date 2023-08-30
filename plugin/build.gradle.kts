plugins {
    id("annostic.conventions-library")
    `kotlin-dsl`
}

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