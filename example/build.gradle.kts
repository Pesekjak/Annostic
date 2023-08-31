plugins {
    id("annostic.conventions-library")
    id("me.pesekjak.annostic-plugin")
}

dependencies {
    implementation(project(":core"))
    annotationProcessor(project(":processor"))
}