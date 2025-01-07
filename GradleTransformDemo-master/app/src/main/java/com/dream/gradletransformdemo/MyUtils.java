package com.dream.gradletransformdemo;

public class MyUtils {

    public int getLength(String str) {
        if(str == null){
            return 0;
        }
        int len = 0;
        for (int i = 0; i < str.length(); i++) {
            len++;
        }

        return len;
    }

    public char[] getCharArray(String str) {
        if(str == null){
            return new char[0];
        }
        return  str.toCharArray();
    }
}