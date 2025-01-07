package com.dream.customtransformplugin

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter

class AUtoInstrumentationMethodVisitor(
    api: Int,
    methodVisitor: MethodVisitor?,
    access: Int,
    name: String?,
    descriptor: String?,
    private val className: String,
) : AdviceAdapter(api, methodVisitor, access, name, descriptor) {

    private var methodName: String = name ?: "unknownMethodName"
    private var fullMethodName: String = "$className.$methodName"

    override fun onMethodEnter() {
        // 方法开始时插入 "我来了****" 的打印语句
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
        mv.visitLdcInsn("我来了**** $fullMethodName")
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false)

        // 方法开始时插入 Trace.beginSection(fullMethodName)
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
        mv.visitLdcInsn("Trace.beginSection: $fullMethodName")
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false)
        mv.visitLdcInsn(fullMethodName)
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/os/Trace", "beginSection", "(Ljava/lang/String;)V", false)
    }

    override fun onMethodExit(opcode: Int) {
        try {
            // 方法结束时插入 Trace.endSection()
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/os/Trace", "endSection", "()V", false)
            mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
            mv.visitLdcInsn("Trace.endSection: $fullMethodName")
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false)
        } catch (e: Exception) {
            e.printStackTrace()
            println("Error in onMethodExit: $fullMethodName, ${e.message}")
        }

    }

    override fun visitMaxs(maxStack: Int, maxLocals: Int) {
        super.visitMaxs(maxStack + 4, maxLocals + 4)
    }
}