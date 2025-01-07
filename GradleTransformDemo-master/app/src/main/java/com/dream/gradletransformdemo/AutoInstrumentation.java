package com.dream.gradletransformdemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AutoInstrumentation {

    public boolean equals(@Nullable Object obj) {
        System.out.println("equals method");
        return true;
    }

    public String toString() {
        System.out.println("toString method");
        return "AutoInstrumentation.string";
    }

    public int testTryCath(int i) {
        System.out.println("testTryCath method, input $i");
        int j = 0;
        try {
            j = 0/i;
        } catch (Exception e) {
            System.out.println(e);
            throw  e;
        }
        return j;
    }

    public void testVoidMethod(){
        for (int i = 0; i < 100; i++) {
            System.out.println("testVoidMethod $i");
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}