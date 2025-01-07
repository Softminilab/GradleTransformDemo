package com.dream.gradletransformdemo;

public class TestUpdatePersonService {

    public static void main(String[] args){
        PersonService personService = new PersonService();
        IPerson iPerson = personService.getIPerson();
        System.out.println("IPersonName : " + iPerson.getName());

    }
}