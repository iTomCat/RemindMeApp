package com.example.tomcat.remindmeapp.sms;


import java.util.ArrayList;

public class Contact {
    public String id;
    public String name;
    public String photo;
    ArrayList<ContactPhone> numbers;

    Contact(String id, String name, String photo) {
        this.id = id;
        this.name = name;
        this.photo = photo;
        this.numbers = new ArrayList<>();
    }

    @Override
    public String toString() {
        String result = name;
        if (numbers.size() > 0) {
            ContactPhone number = numbers.get(0);
            result += " (" + number.number + " - " + number.type + ")";
        }
        return result;
    }

    void addNumber(String number, String type) {
        numbers.add(new ContactPhone(number, type));
    }
}
