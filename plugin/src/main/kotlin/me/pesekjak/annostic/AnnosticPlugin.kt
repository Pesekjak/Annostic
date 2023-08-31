package me.pesekjak.annostic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.task

abstract class AnnosticPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val task = target.task<InjectAnnotationsTask>("injectAnnotations")
        task.doLast { task.modifyBytecode() }
        task.onlyIf { !task.state.executed }

        target.tasks.named("compileJava").get().finalizedBy(task)
    }

}