plugins {
    id("annostic.conventions-library")
}

allprojects {
    group = "me.pesekjak.annostic"
    version = "1.0"

    repositories {
        mavenCentral()
        maven("https://jitpack.io")
    }
}