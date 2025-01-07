package com.dream.customtransformplugin

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
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
    private var tryCatchLabel = newLabel()
    private var finallyLabel = newLabel()
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

        // try 开始
        visitTryCatchBlock(tryCatchLabel,finallyLabel,finallyLabel,"java/lang/Throwable")

    }

    override fun onMethodExit(opcode: Int) {
        //判断方法是正常返回还是抛出异常
        if (opcode == Opcodes.ATHROW) return

        //正常返回插入Trace.endSection
        visitFinallyEnd()
    }

    override fun visitMaxs(maxStack: Int, maxLocals: Int) {
        // finally结束标签
        visitLabel(finallyLabel)
        //  保存异常
        storeLocal(newLocal(Type.getObjectType("java/lang/Throwable")))
        // finally 结束  插入  Trace.endSection
        visitFinallyEnd()
        //  抛出异常
        throwException()
        super.visitMaxs(maxStack + 4, maxLocals + 4)
    }

    private fun  visitFinallyEnd(){
        // 方法结束时插入 Trace.endSection()
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/os/Trace", "endSection", "()V", false)
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
        mv.visitLdcInsn("Trace.endSection: $fullMethodName")
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false)
    }


    override fun visitTryCatchBlock(start: org.objectweb.asm.Label?, end: org.objectweb.asm.Label?, handler: org.objectweb.asm.Label?, type: String?) {
        super.visitTryCatchBlock(start, end, handler, type)
    }
}