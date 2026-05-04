package com.example.eventsapp;

import java.time.LocalDateTime;

public class Event {

    private String name;
    private String description;
    private String location;
    private String dateTime;
    private String category;
    private int imageResld;   // resurs slike
    private boolean isPromoted;
    private int capacity;
    private int attendingCount;
    private double averageRating;
    private int ratingCount;

    // constructor for promoted events
    public Event(String name, String description, String location, String dateTime, String category, int imageResld, boolean isPromoted, int capacity, int attendingCount, double averageRating, int ratingCount){
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
        this.category = category;
    }

    // constructor for regular events
    public Event(String name, String description, String location, String dateTime, String category, int imageResld, boolean isPrompted, int attendingCount, double averageRating, int ratingCount){
        this.name = name;
        this.description = description;
        this.location = location;
        this.dateTime = dateTime;
        this.imageResld = imageResld;
        this.attendingCount = attendingCount;
        this.averageRating = averageRating;
        this.ratingCount = ratingCount;
        this.category = category;
    }

    public boolean isPast(){
        LocalDateTime now = LocalDateTime.now();

        return true;
    }

    public void addRating(int rating){
        this.ratingCount += 1;
        this.averageRating = (this.averageRating+rating)/2;
    }

    public void setName(String Name){
        this.name = name;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public void setLocation(String location){
        this.location = location;
    }

    public void setDateTime(String dateTime){
        this.dateTime = dateTime;
    }

    public void setCategory(String location){
        this.location = location;
    }

    public void setPromoted(boolean isPromoted){
        this.isPromoted = isPromoted;
    }

    public void setCapacity(int capacity){
        this.capacity = capacity;
    }

    public void setAttendingCount(int attendingCount){
        this.attendingCount = attendingCount;
    }

    public void setImageResld(int imageResld){
        this.imageResld = imageResld;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
    public String getLocation() {
        return location;
    }
    public String getCategory() {
        return category;
    }

    public String getDateTime() {
        return dateTime;
    }

    public boolean isPromoted() {
        return isPromoted;
    }

    public int getAttendingCount() {
        return attendingCount;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getImageResld() {
        return imageResld;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public double getAverageRating() {
        return averageRating;
    }
}
