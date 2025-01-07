package com.dream.gradletransformdemo
import android.app.Application

class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        insertByGT()
        updateByGT()
        addByGT()
        deleteByGT()
    }

    fun insertByGT() {
        val i = 11000
        println(i)
    }

    fun updateByGT() {
        val i = 11000
        println(i)
    }

    fun addByGT() {
        val i = 11000
        println(i)
    }

    fun deleteByGT() {
        val i = 11000
        println(i)
    }
}