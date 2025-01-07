package com.dream.customtransformplugin

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationParameters
import org.gradle.api.tasks.Input
import org.objectweb.asm.ClassVisitor

abstract class AutoInstrumentationTransform : AsmClassVisitorFactory<AutoInstrumentationTransform.Parameters> {

    interface Parameters : InstrumentationParameters {
        @get:Input
        val analyticsExtension: org.gradle.api.provider.Property<AnalyticsExtension>

        @get:Input
        val includeClasses: org.gradle.api.provider.Property<String>

        @get:Input
        val includeMethods: org.gradle.api.provider.Property<String>
    }

    override fun createClassVisitor(classContext: ClassContext, nextClassVisitor: ClassVisitor): ClassVisitor {
        return AutoInstrumentationClassVisitor(
            nextClassVisitor,
            parameters.get().analyticsExtension.get(),
            parameters.get().includeClasses.get().split(",").filter { it.isNotBlank() },
            parameters.get().includeMethods.get().split(",").filter { it.isNotBlank() }
        )
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        return shouldInstrumentClass(classData.className)
    }


    private fun shouldInstrumentClass(className: String): Boolean {
        val includeClasses = parameters.get().includeClasses.get().split(",").filter { it.isNotBlank() }
        val shouldInstrument =  !className.contains("Liveliterals") &&  if (includeClasses.isEmpty()) {
            true
        } else {
            includeClasses.any{className.contains(it)}
        }

        println("shouldInstrumentClass: $className, result: $shouldInstrument")
        return shouldInstrument
    }
}