plugins {
    id("annostic.conventions-library")
    `maven-publish`
}

dependencies {
    implementation(project(":core"))
    implementation(libs.google.gson)
    implementation(libs.google.autoservice)
    annotationProcessor(libs.google.autoservice)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "me.pesekjak.annostic"
            artifactId = "processor"
            version = project.version.toString()
            from(components["java"])
        }
    }
}