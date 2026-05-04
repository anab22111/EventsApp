package com.example.eventsapp;

public class EventFactory {

    public static Event createRegularEvent(String name, String description, String location, String dateTime,  String category, int imageResld){
        return new Event(name, description, location, dateTime, category, imageResld);
    }

    public static Event createPromotedEvent(String name, String description, String location, String dateTime, String category, int imageResld, int capacity){
       return new Event(name, description, location, dateTime, true, category, imageResld, capacity);
    }
}
