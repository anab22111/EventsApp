package com.example.eventsapp;

import java.time.LocalDateTime;

public class Event {

    private String name;
    private String description;
    private String location;
    private String dateTime;
    private int imageResld;   // resurs slike
    private boolean isPromoted;
    private int capacity;
    private int attendingCount;
    private double averageRating;
    private int ratingCount;

    // constructor for promoted events
    public Event(String name, String description, String location, String dateTime, int imageResld, boolean isPromoted, int capacity, int attendingCount, double averageRating, int ratingCount){
        this.name = name;
        this.description = description;
        this.location = location;
        this.dateTime = dateTime;
        this.imageResld = imageResld;
        this.isPromoted = isPromoted;
        this.capacity = capacity;
        this.attendingCount = attendingCount;
        this.averageRating = averageRating;
        this.ratingCount = ratingCount;
    }

    // constructor for regular events
    public Event(String name, String description, String location, String dateTime, int imageResld, boolean isPrompted, int attendingCount, double averageRating, int ratingCount){
        this.name = name;
        this.description = description;
        this.location = location;
        this.dateTime = dateTime;
        this.imageResld = imageResld;
        this.attendingCount = attendingCount;
        this.averageRating = averageRating;
        this.ratingCount = ratingCount;
    }

    
}
