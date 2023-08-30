rootProject.name = "Annostic"

pluginManagement {
    includeBuild("build-logic")
    includeBuild("plugin")
}
include("core")
include("example")
include("processor")
include("example-usage")
