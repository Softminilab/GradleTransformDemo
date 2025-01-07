package com.dream.customtransformplugin

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

object FileUtils {


    @Throws(IOException::class)
    fun copyDirectory(srcDir: File, destDir: File) {
        if (!srcDir.exists() || !srcDir.isDirectory) return
        if(!destDir.exists()){
            destDir.mkdirs()
        }

        srcDir.listFiles()?.forEach { file ->
            val destFile = File(destDir,file.name)

            if (file.isFile) {
                copyFile(file,destFile)
            }else if(file.isDirectory){
                copyDirectory(file,destFile)
            }

        }


    }


    @Throws(IOException::class)
    fun copyFile(srcFile: File, destFile: File) {
        val inputStream = FileInputStream(srcFile)
        val outputStream = FileOutputStream(destFile)
        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
    }
}