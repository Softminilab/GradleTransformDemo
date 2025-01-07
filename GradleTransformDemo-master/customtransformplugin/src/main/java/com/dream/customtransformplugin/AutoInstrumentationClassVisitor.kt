package com.dream.customtransformplugin

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class AutoInstrumentationClassVisitor(
    classVisitor: ClassVisitor,
    private val analyticsExtension: AnalyticsExtension,
    private val includeClasses: List<String>,
    private val includeMethods: List<String>,
    private val systemIncludeMethods: List<String>,
    private val projectPackageName: String
) : ClassVisitor(Opcodes.ASM9, classVisitor) {

    private var currentClassName: String? = null


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
        println("visit: $name, superName: $superName")
    }


    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions)
        val shouldInstrument = name != null && descriptor != null &&
                (access and (Opcodes.ACC_SYNTHETIC or Opcodes.ACC_BRIDGE)) == 0 && // 过滤合成和桥接方法
                (shouldInstrumentProjectMethod(name) || shouldInstrumentSystemMethod(name))
        println("visitMethod name: $name descriptor: $descriptor shouldInstrument: $shouldInstrument access: $access")

        return if (shouldInstrument && currentClassName != null) {
            AUtoInstrumentationMethodVisitor(
                Opcodes.ASM9, methodVisitor, access, name, descriptor, currentClassName!!
            )
        } else {
            methodVisitor
        }
    }

    /**
     * 判断是否需要插桩项目的方法
     */
    private fun shouldInstrumentProjectMethod(methodName: String): Boolean {
        val shouldInstrument = if (includeMethods.isEmpty()) {
            currentClassName?.startsWith(projectPackageName.replace(".", "/")) == true
        } else {
            includeMethods.contains(methodName)
        }

        println("shouldInstrumentProjectMethod: $methodName, result: $shouldInstrument")
        return shouldInstrument
    }


    /**
     * 判断是否需要插桩系统的方法
     */
    private fun shouldInstrumentSystemMethod(methodName: String): Boolean {
        val shouldInstrument = if (systemIncludeMethods.isEmpty()) {
            false
        } else {
            systemIncludeMethods.contains(methodName)
        }
        println("shouldInstrumentSystemMethod: $methodName, result: $shouldInstrument")
        return shouldInstrument
    }
}