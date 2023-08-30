plugins {
    id("annostic.conventions-library")
}

dependencies {
    implementation(project(":core"))
    implementation(libs.google.gson)
    implementation(libs.google.autoservice)
    annotationProcessor(libs.google.autoservice)
}