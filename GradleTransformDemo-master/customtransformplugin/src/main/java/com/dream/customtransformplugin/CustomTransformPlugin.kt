package com.dream.customtransformplugin

import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import com.android.build.api.instrumentation.InstrumentationScope
import java.io.FileInputStream
import org.yaml.snakeyaml.Yaml
import java.io.File

class CustomTransformPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        println("Hello CustomTransformPlugin")

        val analyticsExtension = project.extensions.create("analyticsExtension", AnalyticsExtension::class.java)

        val projectPackageName = project.group.toString() // 获取项目的包名


        // 加载 YAML 配置
        // 构建 YAML 配置文件的相对路径
        val configFile = File(project.rootDir, "filter_config.yaml")
        //  val configFile = project.file("filter_config.yaml") //之前使用相对路径的代码
        println("configFile path : ${configFile.absolutePath}")

        val config: Map<String, Any>? = if (configFile.exists()) {
            println("configFile exists ")
            FileInputStream(configFile).use {
                val yaml = Yaml()
                yaml.load(it) as? Map<String, Any>
            }
        } else {
            println("configFile not exists")
            println("空了")
            null
        }

        val includeClasses: List<String> = config?.let { configMap ->
            (configMap["include"] as? Map<String, Any>)?.let { includeMap ->
                (includeMap["classes"] as? List<*>)?.filterIsInstance<String>()
                    ?.also { if (it.size != (includeMap["classes"] as? List<*>)?.size) println("Warning: Some class entries are not string") }
            }
        } ?: emptyList()

        println("includeClasses $includeClasses")

        val includeMethods: List<String> = config?.let { configMap ->
            (configMap["include"] as? Map<String, Any>)?.let { includeMap ->
                (includeMap["methods"] as? List<*>)?.filterIsInstance<String>()
                    ?.also { if (it.size != (includeMap["methods"] as? List<*>)?.size) println("Warning: Some method entries are not string") }
            }
        } ?: emptyList()
        println("includeMethods $includeMethods")

        val systemIncludeMethods: List<String> = config?.let { configMap ->
            (configMap["include"] as? Map<String, Any>)?.let { includeMap ->
                (includeMap["systemMethods"] as? List<*>)?.filterIsInstance<String>()
                    ?.also { if (it.size != (includeMap["systemMethods"] as? List<*>)?.size) println("Warning: Some system method entries are not string") }
            }
        } ?: emptyList()
        println("systemIncludeMethods $systemIncludeMethods")



        val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)
        androidComponents.onVariants { variant ->
            variant.instrumentation.setAsmFramesComputationMode(
                FramesComputationMode.COMPUTE_FRAMES_FOR_INSTRUMENTED_METHODS
            )
            variant.instrumentation.transformClassesWith(
                AutoInstrumentationTransform::class.java,
                InstrumentationScope.PROJECT
            ) {
                it.analyticsExtension.set(analyticsExtension)
                it.includeClasses.set(includeClasses.joinToString(","))
                it.includeMethods.set(includeMethods.joinToString(","))
                it.systemIncludeMethods.set(systemIncludeMethods.joinToString(","))
                it.projectPackageName.set(projectPackageName)
            }
        }
    }
}