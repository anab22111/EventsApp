package com.example.eventsapp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Event {

    private String name, description, location, dateTime, category;
    private int imageResId, capacity, attendingCount, ratingCount, numbOfAttendees;
    private boolean isPromoted;
    private double averageRating;


    // constructor for promoted events
    public Event(String name, String description, String location, String dateTime, boolean isPromoted,  String category, int imageResId, int capacity){
        this.name = name;
        this.description = description;
        this.location = location;
        this.dateTime = dateTime;
        this.imageResId = imageResId;
        this.capacity = capacity;
        this.isPromoted = isPromoted;
        this.category = category;
        this.averageRating = 0;
        this.ratingCount = 0;
        this.numbOfAttendees = 0;
    }

    // constructor for regular events
    public Event(String name, String description, String location, String dateTime, String category, int imageResId){
        this.name = name;
        this.description = description;
        this.location = location;
        this.dateTime = dateTime;
        this.imageResId = imageResId;
        this.category = category;
        this.averageRating = 0;
        this.ratingCount = 0;
        this.numbOfAttendees = 0;

    }

    public boolean isPast(){
        // definition of date and time format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm");

        try {
            // convert string dateTime to LocalDateTime object
            LocalDateTime eventDateTime = LocalDateTime.parse(this.dateTime, formatter);

            // compare to current date and time
            // if true the event has passed
            return eventDateTime.isBefore(LocalDateTime.now());
        } catch (Exception e) {
            // if format of string dateTime isn't correct catch exception
            e.printStackTrace();
            return false;
        }

    }

    public void addRating(int rating){
        this.ratingCount += 1;
        this.averageRating = (this.averageRating+rating)/2;
    }

    public void setName(String name){
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

    public void setCategory(String category){
        this.category = category;
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

    public void setImageResld(int imageResId){
        this.imageResId = imageResId;
    }

    public void setAverageRating(double averageRating){this.averageRating = averageRating;}

    public void setRatingCount(int ratingCount){this.ratingCount = ratingCount;}
    public void setNumberOfAttendees(int num){this.numbOfAttendees = num;}


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

    public int getImageResId() {
        return imageResId;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public double getAverageRating() {
        return averageRating;
    }
    public double getNumberOfAttendees() {
        return numbOfAttendees;
    }
}

