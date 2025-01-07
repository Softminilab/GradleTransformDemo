package com.dream.gradletransformdemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dream.gradletransformdemo.annotation.CostTime

class MainActivity : AppCompatActivity() {

    @CostTime
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        println("MainActivity onCreate start")
        performCalculationsByGT()
        testAutoInstrumentationByGT()
        performStringOperationsByGT()
        println("MainActivity onCreate end")
    }

    fun performCalculationsByGT(){
        val number = 1000
        println("MainActivity")
        println("i value: $number")

        val start = System.currentTimeMillis()
        var sum = 0
        for (i in 0..number) {
            sum += i
            sum -= (i / 2)
        }
        println("sum: $sum")
        val time =  System.currentTimeMillis() - start
        println("计算 耗时 time:  $time ms")
    }

    fun testAutoInstrumentationByGT(){
        val  a = AutoInstrumentation()
        val string = a.toString()
        print("string $string")
        a.testTryCath(1)
    }


    fun performStringOperationsByGT(){
        val myUtils = MyUtils()
        val charArray = myUtils.getCharArray("hello")
        println("charArray length: ${charArray.size}")

        val length = myUtils.getLength("world")
        println("length: $length")

        PersonService().personFly()
    }
}