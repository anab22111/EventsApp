package com.example.eventsapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class dbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "EventsApp.db";
    private static final int DATABASE_VERSION = 1;



    public dbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // called only once when the database is created for the first time
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create all the tables

        // users table
        String createUsersTable = "CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT UNIQUE NOT NULL, " +
                "email TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL);";
        db.execSQL(createUsersTable);

        // events table
        String createEventsTable = "CREATE TABLE events (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "description TEXT, " +
                "location TEXT NOT NULL, " +
                "dateTime TEXT NOT NULL, " +
                "category TEXT NOT NULL, " +
                "promoted INTEGER DEFAULT 0 CHECK (promoted IN (0, 1)), " +
                "capacity INTEGER DEFAULT 0 CHECK (capacity >= 0), " +
                "numberOfAttendees INTEGER DEFAULT 0 CHECK (numberOfAttendees >= 0), " +   // number of registered users
                "avgRating REAL DEFAULT 0 CHECK (avgRating BETWEEN 0 AND 5), " +
                "numberOfRatings INTEGER DEFAULT 0 CHECK (numberOfRatings >= 0), "+
                "CHECK (promoted = 0 OR capacity > 0), " +    // if event promoted then capacity greater then 0
                "CHECK (promoted = 0 OR numberOfAttendees <= capacity));";    // for promoted events numb of Attendees can't go over capacity
        db.execSQL(createEventsTable);

        // attendance table
        String createAttendanceTable = "CREATE TABLE attendance (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "userId INTEGER NOT NULL, " +
                "eventId INTEGER NOT NULL, " +
                "commitment TEXT NOT NULL, " +
                // rules
                "FOREIGN KEY(userId) REFERENCES users(id), " +
                "FOREIGN KEY(eventId) REFERENCES events(id), " +
                "UNIQUE(userId, eventId), " +
                "CHECK(commitment IN ('INTERESTED', 'ATTENDING')));";
        db.execSQL(createAttendanceTable);

        // ratings table
        String CreateRatingsTable = "CREATE TABLE ratings (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "userId INTEGER NOT NULL, " +
                "eventId INTEGER NOT NULL, " +
                "rating INTEGER NOT NULL CHECK (rating BETWEEN 1 AND 5), " +
                // rules
                "FOREIGN KEY(userId) REFERENCES users(id), " +
                "FOREIGN KEY(eventId) REFERENCES events(id), " +
                "UNIQUE(userId, eventId));";
        db.execSQL(CreateRatingsTable);

        insertEvents(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS ratings");
        db.execSQL("DROP TABLE IF EXISTS attendance");
        db.execSQL("DROP TABLE IF EXISTS events");
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
        super.onConfigure(db);
    }

    private void insertEvents(SQLiteDatabase db){

        // insert events into the table
        db.execSQL("INSERT INTO events (name, description, location, dateTime, category, promoted, capacity) " +
                "VALUES ('Ultra Europe', 'Music festival', 'Split, Croatia', '10.07.2026. 20:00', 'Festival', 1, 50000);");

        db.execSQL("INSERT INTO events (name, description, location, dateTime, category, promoted, capacity) " +
                "VALUES ('Champions League Final', 'Greatest final ever', 'Spain, Madrid', '25.08.2026. 21:00', 'Football', 1, 400000);");

        db.execSQL("INSERT INTO events (name, description, location, dateTime, category, promoted, capacity) " +
                "VALUES ('Exit', 'Biggest music festival in Serbia', 'Petrovaradin, Novi Sad', '15.07.2026. 18:00', 'Festival', 0, 0);");

        db.execSQL("INSERT INTO events (name, description, location, dateTime, category, promoted, capacity) " +
                "VALUES ('Things unfinished', 'Maja Momirov exhibition', 'Petrovaradin, Novi Sad', '20.12.2025. 18:00', 'Exhibition', 0, 0);");

        db.execSQL("INSERT INTO events (name, description, location, dateTime, category, promoted, capacity) " +
                "VALUES ('Fruska gora marathon', '49th Fruska Gora marathon', 'Fruska Gora, Popovica', '25.04.2026. 09:00', 'Marathon', 0, 0);");

        db.execSQL("INSERT INTO events (name, description, location, dateTime, category, promoted, capacity) " +
                "VALUES ('Mike Stand-up', 'First time in Niš', 'Niš, Serbia', '15.02.2024. 20:00', 'Stand-Up & Theater', 0, 0);");

        db.execSQL("INSERT INTO events (name, description, location, dateTime, category, promoted, capacity) " +
                "VALUES ('NEON party', 'The best party is back!', 'Štark Arena, Belgrade', '05.02.2024. 20:00', 'Party', 1, 200);");

        db.execSQL("INSERT INTO events (name, description, location, dateTime, category, promoted, capacity) " +
                "VALUES ('Durmitor marathon', '50th Durmitor marathon', 'Durmitor, Montenegro', '25.08.2026. 09:00', 'Marathon', 0, 0);");

        db.execSQL("INSERT INTO events (name, description, location, dateTime, category, promoted, capacity) " +
                "VALUES ('Coldplay Live', 'Music of the Spheres World Tour', 'Wembley Stadium, London', '12.06.2026. 20:00', 'Concert', 0, 0);");

        db.execSQL("INSERT INTO events (name, description, location, dateTime, category, promoted, capacity) " +
                "VALUES ('Beer Fest', 'Belgrade traditional beer and music festival', 'Ušće, Belgrade', '15.08.2026. 18:00', 'Festival', 0, 0);");

        db.execSQL("INSERT INTO events (name, description, location, dateTime, category, promoted, capacity) " +
                "VALUES ('Techno Warehouse', 'Underground techno experience', 'Petrovaradin, Novi Sad', '05.12.2026. 23:00', 'Party', 0, 0);");

        db.execSQL("INSERT INTO events (name, description, location, dateTime, category, promoted, capacity) " +
                "VALUES ('Van Gogh Experience', 'Immersive digital art exhibition', 'Hala 4, Beogradski Sajam', '10.12.2026. 12:00', 'Exhibition', 0, 0);");

        db.execSQL("INSERT INTO events (name, description, location, dateTime, category, promoted, capacity) " +
                "VALUES ('Belgrade Marathon', '42km race through the city', 'Center, Belgrade', '17.05.2026. 08:00', 'Marathon', 0, 0);");

        db.execSQL("INSERT INTO events (name, description, location, dateTime, category, promoted, capacity) " +
                "VALUES ('Wine Fest 2024', 'Annual wine tasting event', 'City Center, Novi Sad', '15.03.2024. 14:00', 'Festival', 0, 0);");

        db.execSQL("INSERT INTO events (name, description, location, dateTime, category, promoted, capacity) " +
                "VALUES ('Državni Posao', 'Live comedy show with the famous trio', 'Srpsko narodno pozorište, Novi Sad', '15.11.2026. 20:00', 'Stand-Up & Theater', 0, 0);");

        db.execSQL("INSERT INTO events (name, description, location, dateTime, category, promoted, capacity) " +
                "VALUES ('Nikola Djuricko Show', 'Showbiz stand-up comedy', 'SNP, Novi Sad', '20.10.2026. 20:00', 'Stand-Up & Theater', 0, 0);");

        db.execSQL("INSERT INTO events (name, description, location, dateTime, category, promoted, capacity) " +
                "VALUES ('Hamlet', 'Modern interpretation of Shakespeare', 'JDP, Belgrade', '25.11.2026. 20:00', 'Stand-Up & Theater', 0, 0);");

        db.execSQL("INSERT INTO events (name, description, location, dateTime, category, promoted, capacity) " +
                "VALUES ('World Cup Semi Final', 'Spain - France', 'USA, Miami', '30.11.2026. 20:00', 'Football', 0, 0);");

    }
}
