plugins {
    `kotlin-dsl`
}

group = "me.pesekjak.annostic"
version = "1.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://plugins.gradle.org/m2/")
    }
}

dependencies {
    implementation(libs.jetbrains.kotlin.gradle)
    compileOnly(files(libs::class.java.superclass.protectionDomain.codeSource.location))
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "17"
        }
    }
}