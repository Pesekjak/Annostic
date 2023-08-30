import me.pesekjak.annostic.InjectAnnotationsTask

val modifyJarTask = task<InjectAnnotationsTask>("injectAnnotations") {
    doLast {
        modifyBytecode()
    }
    onlyIf {
        !state.executed
    }
}

tasks {
    named("compileJava") {
        finalizedBy(modifyJarTask)
    }
}