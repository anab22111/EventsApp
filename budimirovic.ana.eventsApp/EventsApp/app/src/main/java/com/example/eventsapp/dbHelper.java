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
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }
}
