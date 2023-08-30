pluginManagement {
    includeBuild("../build-logic")
    includeBuild("../plugin")
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

include(":core")
project(":core").projectDir = file("../core")
include(":processor")
project(":processor").projectDir = file("../processor")