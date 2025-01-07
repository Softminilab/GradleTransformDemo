package com.dream.customtransformplugin

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class AutoInstrumentationClassVisitor(
    classVisitor: ClassVisitor,
    private val analyticsExtension: AnalyticsExtension,
    private val includeClasses: List<String>,
    private val includeMethods: List<String>,
) : ClassVisitor(Opcodes.ASM9, classVisitor) {

    private var currentClassName: String? = null
    private var isDataClass: Boolean = false


    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        super.visit(version, access, name, signature, superName, interfaces)
        currentClassName = name
        isDataClass = name != null && superName != null && name.endsWith("Kt")  && superName == "java/lang/Object" && interfaces?.isEmpty() == true  &&  (access and Opcodes.ACC_FINAL) != 0
        println("visit: $name, superName: $superName, isDataClass: $isDataClass")
    }


    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions)
        val shouldInstrument = name != null && descriptor != null && shouldInstrumentMethod(name) && !isDataClass
        println("visitMethod name: $name descriptor: $descriptor shouldInstrument: $shouldInstrument")

        return if (shouldInstrument && currentClassName != null) {
            AUtoInstrumentationMethodVisitor(
                Opcodes.ASM9, methodVisitor, access, name, descriptor, currentClassName!!
            )
        } else {
            methodVisitor
        }
    }


    /**
     * 判断是否需要插桩的方法
     */
    private fun shouldInstrumentMethod(methodName: String): Boolean {
        println("includeMethods $includeMethods")

        val shouldInstrument = if (includeMethods.isEmpty()) {
            true
        } else {
            includeMethods.contains(methodName)
        }

        println("shouldInstrumentMethod: $methodName, result: $shouldInstrument")
        return shouldInstrument
    }
}