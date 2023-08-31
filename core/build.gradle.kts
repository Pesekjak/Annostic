plugins {
    id("annostic.conventions-library")
    `maven-publish`
}

java {
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "me.pesekjak.annostic"
            artifactId = "core"
            version = project.version.toString()
            from(components["java"])
            artifact("sourcesJar")
        }
    }
}