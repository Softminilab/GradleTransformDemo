package com.dream.gradletransformdemo;

public class PersonService {

    public void personFly() {
        for (int i = 0; i < 10; i++) {
            System.out.println("person is fly" + i);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public IPerson getIPerson(){
        return new IPerson() {
            @Override
            public String getName() {
                return "person";
            }
        };
    }
}