package com.example.eventsapp;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class AppData {
    public static List<Event> allEvents = new ArrayList<>();
    public static List<Event> interestedEvents = new ArrayList<>();
    public static List<Event> attendingEvents = new ArrayList<>();

    // manually filling lists in a static block
    //static block is run only once
    static{
        // adding events to allEvents list
        allEvents.add(EventFactory.createPromotedEvent("Ultra Europe", "Music festival", "Split, Croatia", "10.07.2026. 20:00", "Festival", 101, 50000));
        allEvents.add(EventFactory.createPromotedEvent("Champions League Final", "Greatest final ever", "USA, Miami", "25.08.2026. 21:00", "Football", 101, 400000));
        allEvents.add(EventFactory.createRegularEvent("Exit", "Biggest music festival in Serbia", "Petrovaradin, Novi Sad", "15.07.2026. 18:00", "Festival", 1));
        allEvents.add(EventFactory.createRegularEvent("Things unfinished", "Maja Momirov exhibition", "Petrovaradin, Novi Sad", "20.12.2025. 18:00", "Exhibition", 1));  // prosao
        allEvents.add(EventFactory.createRegularEvent("Fruska gora marathon", "49th Fruska Gora marathon", "Fruska Gora, Popovica", "25.04.2026. 09:00", "Marathon", 1));  //prosao
        allEvents.add(EventFactory.createRegularEvent("Mike Stand-up", "First time in Niš", "Niš, Serbia", "15.02.2024. 20:00", "Stand-Up & Theater", 104));  // prosao
        allEvents.add(EventFactory.createPromotedEvent("NEON party", "The best party is back!", "Štark Arena, Belgrade", "05.02.2024. 20:00", "Party", 104, 200)); // prosao
        allEvents.add(EventFactory.createRegularEvent("Durmitor marathon", "50th Durmitor marathon", "Durmitor, Montenegro", "25.08.2026. 09:00", "Marathon", 1));
        allEvents.add(EventFactory.createRegularEvent("Coldplay Live", "Music of the Spheres World Tour", "Wembley Stadium, London", "12.06.2026. 20:00", "Concert", 105));
        allEvents.add(EventFactory.createRegularEvent("Digital Art Expo", "Future of digital creativity", "Museum of Contemporary Art, Belgrade", "15.09.2026. 10:00", "Exhibition", 106));
        allEvents.add(EventFactory.createRegularEvent("Beer Fest", "Belgrade traditional beer and music festival", "Ušće, Belgrade", "15.08.2026. 18:00", "Festival", 109));
        allEvents.add(EventFactory.createRegularEvent("Techno Warehouse", "Underground techno experience", "Petrovaradin, Novi Sad", "05.12.2026. 23:00", "Party", 112));
        allEvents.add(EventFactory.createRegularEvent("Van Gogh Experience", "Immersive digital art exhibition", "Hala 4, Beogradski Sajam", "10.12.2026. 12:00", "Exhibition", 116));
        allEvents.add(EventFactory.createRegularEvent("Belgrade Marathon", "42km race through the city", "Center, Belgrade", "17.05.2026. 08:00", "Marathon", 120));
        allEvents.add(EventFactory.createRegularEvent("Wine Fest 2024", "Annual wine tasting event", "City Center, Novi Sad", "15.03.2024. 14:00", "Festival", 119));
        allEvents.add(EventFactory.createRegularEvent("Državni Posao", "Live comedy show with the famous trio", "Srpsko narodno pozorište, Novi Sad", "15.11.2026. 20:00", "Stand-Up & Theater", 115));
        allEvents.add(EventFactory.createRegularEvent("Nikola Djuricko Show", "Showbiz stand-up comedy", "SNP, Novi Sad", "20.10.2026. 20:00", "Stand-Up & Theater", 108));
        allEvents.add(EventFactory.createRegularEvent("Hamlet", "Modern interpretation of Shakespeare", "JDP, Belgrade", "25.11.2026. 20:00", "Stand-Up & Theater", 113));

        // adding events to interestedEvents
        interestedEvents.add(allEvents.get(0)); // Ultra Europe
        interestedEvents.add(allEvents.get(2)); // Exit
        interestedEvents.add(allEvents.get(8)); // Coldplay
        interestedEvents.add(allEvents.get(10)); // Summer Foam
        interestedEvents.add(allEvents.get(12)); // Beer Fest

        // adding events to attendingEvents
        attendingEvents.add(allEvents.get(0));
        attendingEvents.add(allEvents.get(1));
        attendingEvents.add(allEvents.get(3)); // passed (Things unfinished)
        attendingEvents.add(allEvents.get(4)); // passed (Fruska gora)
        attendingEvents.add(allEvents.get(5)); // passed (Mike)
        attendingEvents.add(allEvents.get(6)); // passed (Neon)
        attendingEvents.add(allEvents.get(8));
        attendingEvents.add(allEvents.get(9));

    }

    public static List<Event> getSortedEvents() {
        // array list to stream of data
        return allEvents.stream()
                .sorted(Comparator.comparing(Event::isPromoted, Comparator.reverseOrder()))   // sort by promotion of an event, in reverse because java takes false as the firsts
                .collect(Collectors.toList());    // collect all the data from the stream
    }

    public static List<Event> getEventsByCategory(String category) {
        return allEvents.stream()
                .filter(e -> e.getCategory().equalsIgnoreCase(category)) // look only at events with the correct category
                .sorted(Comparator.comparing(Event::getDateTime))        // sort by date
                .collect(Collectors.toList());                           // return new list with sorted events with the forwarded category
    }
    public static Event findByName(String name) {
        //find event by name
        for (Event e : allEvents) {
            if (e.getName().equalsIgnoreCase(name)) {
                return e;
            }
        }
        return null; // if not found return null
    }

}
