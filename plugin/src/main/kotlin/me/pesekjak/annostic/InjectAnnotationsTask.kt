package me.pesekjak.annostic

import com.google.gson.JsonParser
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.objectweb.asm.*
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.commons.Method
import java.io.File
import java.io.FileInputStream
import java.io.FileReader
import java.io.IOException
import java.nio.file.Files
import kotlin.io.path.name

open class InjectAnnotationsTask : DefaultTask() {

    @Internal
    var properties = HashMap<String, String>()

    private var executed = false

    @TaskAction
    fun modifyBytecode() {
        if (executed) return
        executed = true

        val buildDir = project.layout.buildDirectory.asFile.orNull ?: error("Cannot access the build directory")
        val classesDir = File(buildDir, "classes")
        if (!classesDir.exists()) error("Classes directory does not exist")

        val properties = Files.walk(classesDir.toPath(), Int.MAX_VALUE)
            .filter { file -> file.name == "annostic.json" }
            .findFirst()
            .map { path -> path.toFile() }
            .get()

        loadProperties(properties)

        this.properties.forEach { (target, source) -> visit(properties.parentFile, target, source) }
    }

    private fun loadProperties(file: File) {
        JsonParser.parseReader(FileReader(file)).asJsonObject.entrySet().forEach { (target, source) -> properties[target] = source.asString }
    }

    private fun visit(sourcesDir: File, target: String, source: String) {
        val targetFile = File(sourcesDir, "$target.class")
        println(targetFile.absolutePath)
        if (!targetFile.exists()) error("Failed to find target class '$target' for '$source'")
        val targetType = Type.getType("L$target;")

        val sourceFile = File(sourcesDir, "$source.class")
        if (!sourceFile.exists()) error("Failed to find source class '$source'")
        val sourceType = Type.getType("L$source;")

        val sourceVisitor = SourceVisitor.readAndVisit(ASM9, sourceType, sourceFile)
        sourceFile.writeBytes(sourceVisitor.toByteArray())

        val targetVisitor = TargetVisitor.readAndVisit(ASM9, targetType, sourceType, sourceVisitor.methods, targetFile)
        targetFile.writeBytes(targetVisitor.toByteArray())
    }


    class SourceVisitor(api: Int, private val source: Type) : ClassVisitor(api, ClassWriter(ClassWriter.COMPUTE_MAXS or ClassWriter.COMPUTE_FRAMES)) {

        val methods: MutableList<MethodReference> = ArrayList()

        companion object {
            fun readAndVisit(api: Int, source: Type, classFile: File): SourceVisitor {
                val visitor = SourceVisitor(api, source)
                try {
                    FileInputStream(classFile).use { input -> ClassReader(input).accept(visitor, 0) }
                } catch (exception: IOException) {
                    throw RuntimeException(exception)
                }
                return visitor
            }
        }

        override fun getDelegate(): ClassWriter {
            return super.getDelegate() as ClassWriter
        }

        override fun visit(
            version: Int,
            access: Int,
            name: String,
            signature: String?,
            superName: String,
            interfaces: Array<String>
        ) {
            var classAccess = access
            if (name == source.internalName) classAccess = classAccess or ACC_SYNTHETIC

            super.visit(version, classAccess, name, signature, superName, interfaces)
        }

        override fun visitMethod(
            access: Int,
            name: String,
            descriptor: String,
            signature: String?,
            exceptions: Array<String>?
        ): MethodVisitor {
            var methodAccess = access
            if (methodAccess and ACC_STATIC != 0) {
                methods.add(MethodReference(Method(name, descriptor), signature, exceptions))
                methodAccess = methodAccess or ACC_SYNTHETIC
            }

            return super.visitMethod(methodAccess, name, descriptor, signature, exceptions)
        }

        fun toByteArray(): ByteArray {
            return delegate.toByteArray()
        }

    }


    class TargetVisitor(api: Int, private val target: Type, private val source: Type, methods: List<MethodReference>?) : ClassVisitor(api, ClassWriter(ClassWriter.COMPUTE_MAXS or ClassWriter.COMPUTE_FRAMES)) {

        private val methods: MutableList<MethodReference> = ArrayList()

        companion object {
            fun readAndVisit(
                api: Int,
                target: Type,
                source: Type,
                methods: List<MethodReference>,
                classFile: File
            ): TargetVisitor {
                val visitor = TargetVisitor(api, target, source, methods)
                try {
                    FileInputStream(classFile).use { input -> ClassReader(input).accept(visitor, 0) }
                } catch (exception: IOException) {
                    throw java.lang.RuntimeException(exception)
                }
                return visitor
            }
        }

        init {
            this.methods.addAll(methods!!)
        }

        override fun getDelegate(): ClassWriter {
            return super.getDelegate() as ClassWriter
        }

        override fun visit(
            version: Int,
            access: Int,
            name: String,
            signature: String?,
            superName: String,
            interfaces: Array<String>
        ) {
            super.visit(version, access, name, signature, superName, interfaces)
            if (name == target.internalName) {
                methods.forEach { method ->
                    visitStaticMethod(
                        method
                    )
                }
                methods.clear()
            }
        }

        private fun visitStaticMethod(methodRef: MethodReference) {
            val method = methodRef.method
            val methodVisitor = visitMethod(ACC_PUBLIC or ACC_STATIC, method.name, method.descriptor, methodRef.signature, methodRef.exceptions)
            methodVisitor.visitCode()

            var index = 0
            for (argument in method.argumentTypes) {
                methodVisitor.visitVarInsn(argument.getOpcode(ILOAD), index)
                index += argument.size
            }

            methodVisitor.visitMethodInsn(
                INVOKESTATIC,
                source.internalName,
                method.name,
                method.descriptor,
                true
            )

            methodVisitor.visitInsn(method.returnType.getOpcode(IRETURN))
            methodVisitor.visitMaxs(0, 0)

            methodVisitor.visitEnd()
            visitEnd()
        }

        fun toByteArray(): ByteArray {
            return delegate.toByteArray()
        }

    }

    data class MethodReference(val method: Method, val signature: String?, val exceptions: Array<String>?)


}