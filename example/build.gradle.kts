plugins {
    id("annostic.conventions-library")
    id("annostic.plugin")
}

dependencies {
    implementation(project(":core"))
    annotationProcessor(project(":processor"))
}