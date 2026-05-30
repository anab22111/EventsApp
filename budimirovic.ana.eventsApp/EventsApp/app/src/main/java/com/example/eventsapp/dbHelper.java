package com.example.eventsapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class dbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "EventsApp.db";
    private static final int DATABASE_VERSION = 5;

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
                "password TEXT NOT NULL, " +
                "server_id TEXT, " +
                "admin INTEGER DEFAULT 0);";
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
                "server_id TEXT, " +
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

    public ArrayList<Event> getSortedEvents(){
        // make list to store events from database
        ArrayList<Event> list = new ArrayList<>();
        // open database
        SQLiteDatabase db = this.getReadableDatabase();

        // sort so that promoted events are first - descending from 1 to 0
        Cursor cursor = db.rawQuery("SELECT * FROM events ORDER BY promoted DESC", null);

        if (cursor.moveToFirst()) {   // if there is a table
            do {
                // get all the columns from one row (event)
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                String location = cursor.getString(cursor.getColumnIndexOrThrow("location"));
                String dateTime = cursor.getString(cursor.getColumnIndexOrThrow("dateTime"));
                String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
                int promoted = cursor.getInt(cursor.getColumnIndexOrThrow("promoted"));
                int capacity = cursor.getInt(cursor.getColumnIndexOrThrow("capacity"));
                int numberOfAttendees = cursor.getInt(cursor.getColumnIndexOrThrow("numberOfAttendees"));

                int imageId = getImageRes(category);

                Event event;

                if(promoted == 1){                  // make promoted event
                    event = EventFactory.createPromotedEvent(name, description, location, dateTime, category, imageId, capacity);
                }else{                               // make regular event
                    event = EventFactory.createRegularEvent(name, description, location, dateTime, category, imageId);
                }

                event.setNumberOfAttendees(numberOfAttendees);

                list.add(event);

            } while (cursor.moveToNext()); // moveToNext moves to next row in table
        }

        cursor.close();
        return list;
    }

    public ArrayList<Event> getEventsByCategory(String cat){
        // make list to store events from database
        ArrayList<Event> list = new ArrayList<>();
        // open database
        SQLiteDatabase db = this.getReadableDatabase();

        // sort only events from category cat, so that promoted events are first
        Cursor cursor = db.rawQuery("SELECT * FROM events WHERE category = ? ORDER BY promoted DESC", new String[]{cat});

        if (cursor.moveToFirst()) {   // if there is a table
            do {
                // get all the columns from one row (event)
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                String location = cursor.getString(cursor.getColumnIndexOrThrow("location"));
                String dateTime = cursor.getString(cursor.getColumnIndexOrThrow("dateTime"));
                String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
                int promoted = cursor.getInt(cursor.getColumnIndexOrThrow("promoted"));
                int capacity = cursor.getInt(cursor.getColumnIndexOrThrow("capacity"));
                int numberOfAttendees = cursor.getInt(cursor.getColumnIndexOrThrow("numberOfAttendees"));

                int imageId = getImageRes(category);

                Event event;

                if(promoted == 1){                  // make promoted event
                    event = EventFactory.createPromotedEvent(name, description, location, dateTime, category, imageId, capacity);
                }else{                               // make regular event
                    event = EventFactory.createRegularEvent(name, description, location, dateTime, category, imageId);
                }

                event.setNumberOfAttendees(numberOfAttendees);

                list.add(event);

            } while (cursor.moveToNext()); // moveToNext moves to next row in table
        }

        cursor.close();
        return list;
    }

    public Event getEventByName(String eventName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Event event = null;

        Cursor cursor = db.rawQuery("SELECT * FROM events WHERE name = ?", new String[]{eventName});

        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
            String location = cursor.getString(cursor.getColumnIndexOrThrow("location"));
            String dateTime = cursor.getString(cursor.getColumnIndexOrThrow("dateTime"));
            String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
            int promoted = cursor.getInt(cursor.getColumnIndexOrThrow("promoted"));
            int capacity = cursor.getInt(cursor.getColumnIndexOrThrow("capacity"));
            float avgRating = cursor.getFloat(cursor.getColumnIndexOrThrow("avgRating"));
            int numberOfAttendees = cursor.getInt(cursor.getColumnIndexOrThrow("numberOfAttendees"));
            int numberOfRatings = cursor.getInt(cursor.getColumnIndexOrThrow("numberOfRatings"));

            int imageId = getImageRes(category);

            if (promoted == 1) {
                event = EventFactory.createPromotedEvent(name, description, location, dateTime, category, imageId, capacity);
            } else {
                event = EventFactory.createRegularEvent(name, description, location, dateTime, category, imageId);
            }

            event.setAverageRating(avgRating);
            event.setNumberOfAttendees(numberOfAttendees);
            event.setRatingCount(numberOfRatings);
        }
        cursor.close();
        return event;
    }


    public ArrayList<Event> getEventsByCommitment(String username, String commitment){
        ArrayList<Event> list = new ArrayList<>();
        // open database
        SQLiteDatabase db = this.getReadableDatabase();

        int userId = getUserId(db, username);
        if (userId == -1) return list;    // if user has no events saved return empty list

        // connect events and attendance with userId
        String query = "SELECT e.* FROM events e " +         // all columns form events
                "JOIN attendance a ON e.id = a.eventId " +   // connect tables where eventId is the same
                "WHERE a.userId = ? AND a.commitment = ? " +    // filter new table - take only the ones with certain userId and commitment
                "ORDER BY e.promoted DESC";                     // go through in descending oder by promoted

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), commitment});

        if (cursor.moveToFirst()) {   // if there is a table
            do {
                // get all the columns from one row (event)
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                String location = cursor.getString(cursor.getColumnIndexOrThrow("location"));
                String dateTime = cursor.getString(cursor.getColumnIndexOrThrow("dateTime"));
                String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
                int promoted = cursor.getInt(cursor.getColumnIndexOrThrow("promoted"));
                int capacity = cursor.getInt(cursor.getColumnIndexOrThrow("capacity"));

                int imageId = getImageRes(category);

                int numberOfAttendees = cursor.getInt(cursor.getColumnIndexOrThrow("numberOfAttendees"));

                Event event;

                if(promoted == 1){
                    event = EventFactory.createPromotedEvent(name, description, location, dateTime, category, imageId, capacity);
                }else{
                    event = EventFactory.createRegularEvent(name, description, location, dateTime, category, imageId);
                }

                event.setNumberOfAttendees(numberOfAttendees);
                list.add(event);

            } while (cursor.moveToNext()); // moveToNext moves to next row in table
        }

        cursor.close();

        return list;

    }

    public boolean checkIfAttendanceExists(String username, String eventName, String commitment) {
        SQLiteDatabase db = this.getReadableDatabase();

        int userId = getUserId(db, username);       // get user id

        int eventId = getEventId(db, eventName);       // get event id

        // prepare arguments for query
        String[] columns = {"id"};
        String selection = "userId = ? AND eventId = ? AND commitment = ?";
        String[] selectionArgs = {String.valueOf(userId), String.valueOf(eventId), commitment};

        // make query to find row with correct commitment, event and user id
        Cursor cursor = db.query(
                "attendance",      // name of table
                columns,           // columns needed
                selection,         // WHERE part
                selectionArgs,     // values for WHERE
                null, null, null   // groupBy, having, orderBy
        );

        // if user clicked attending/interested(commitment) cursor will have found the row and it will be 1, otherwise 0
        boolean exists = cursor.getCount() > 0;   // getCount() returns number of rows
        cursor.close();
        return exists;
    }

    public boolean insertAttendance(String username, String eventName, String commitment) {
        SQLiteDatabase db = this.getWritableDatabase();

        int userId = getUserId(db, username);       // get user id

        int eventId = getEventId(db, eventName);       // get event id

        if (userId == -1 || eventId == -1) return false;         // user and event not found

        // get previous commitment of the event, before status is overridden
        String previousCommitment = getPreviousCommitment(db, userId, eventId);

        // make content values to insert new row
        ContentValues values = new ContentValues();
        values.put("userId", userId);
        values.put("eventId", eventId);
        values.put("commitment", commitment);

        // override status
        long result = db.insertWithOnConflict("attendance", null, values, SQLiteDatabase.CONFLICT_REPLACE);

        // change number of attendees if needed

        if (result != -1) {
            if (previousCommitment == null) {
                // if previous nothing, check if current is attending
                if (commitment.equals("ATTENDING")) {

                    updateAttendeeCount(db, eventId, 1);    // increase number
                }
            } else {
                // if previous isn't null, compare with interested and attending
                if (previousCommitment.equals("INTERESTED") && commitment.equals("ATTENDING")) {
                    updateAttendeeCount(db, eventId, 1);
                } else if (previousCommitment.equals("ATTENDING") && commitment.equals("INTERESTED")) {
                    updateAttendeeCount(db, eventId, -1);
                }
            }
        }

        return result != -1;
    }

    private String getPreviousCommitment(SQLiteDatabase db, int userId, int eventId ){
        String comm = null;

        // from table get status, for correct userId and eventId
        Cursor cursor = db.rawQuery("SELECT commitment FROM attendance WHERE userId = ? AND eventId = ?", new String[]{String.valueOf(userId), String.valueOf(eventId)});

        if(cursor.moveToFirst()){
            comm = cursor.getString(0);    // only commitment column selected, hence getString(0)
        }
        //if there is no row for eventId and userId comm remains be null

        cursor.close();
        return comm;
    }

    private int getUserId(SQLiteDatabase db, String username){

        int id = -1;
        Cursor userCursor = db.rawQuery("SELECT id FROM users WHERE username = ?", new String[]{username});
        if (userCursor.moveToFirst()) {
            id = userCursor.getInt(0);    // id is at column 0 always
        }
        userCursor.close();

        return id;
    }

    private int getEventId(SQLiteDatabase db, String eventName){

        int id = -1;
        Cursor eventCursor = db.rawQuery("SELECT id FROM events WHERE name = ?", new String[]{eventName});
        if (eventCursor.moveToFirst()) {
            id = eventCursor.getInt(0);       // id is at column 0 always
        }
        eventCursor.close();
        return id;
    }

    private void updateAttendeeCount(SQLiteDatabase db, int eventId, int amount) {
        db.execSQL("UPDATE events SET numberOfAttendees = numberOfAttendees + (" + amount + ") WHERE id = ?", new String[]{String.valueOf(eventId)});
    }


    public boolean checkIfRatingExists( String username, String eventName){
        SQLiteDatabase db = this.getReadableDatabase();     // get database

        int userId = getUserId(db, username);
        int eventId = getEventId(db, eventName);

        if (userId == -1 || eventId == -1) {       // if user or event isn't found
            return false;
        }


        String query = "SELECT id FROM ratings WHERE userId = ? AND eventId = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId),String.valueOf(eventId)});

        // check if there is a row
        boolean exists = cursor.moveToFirst();  // if there is a row there is a rating

        cursor.close();

        return exists;
    }

    public boolean insertRating(String username, String eventName, int rating, double newAvg){
        SQLiteDatabase db = this.getWritableDatabase();     // get database

        int userId = getUserId(db, username);
        int eventId = getEventId(db, eventName);

        if (userId == -1 || eventId == -1) {       // if user or event isn't found
            return false;
        }

        ContentValues values = new ContentValues();
        values.put("userId", userId);
        values.put("eventId", eventId);
        values.put("rating", rating);

        // insert into ratings table
        long result = db.insert("ratings", null, values);

        // update avgRating and numberOfRatings in events table for that event
        if (result != -1) {       // if rating was successful
            db.execSQL("UPDATE events SET " +
                    "avgRating = ?, " +
                    "numberOfRatings = numberOfRatings + 1 " +
                    "WHERE id = ?", new Object[]{newAvg, eventId});
            return true;
        }

        return false;
    }

    public int getImageRes(String category){
        int imageRes = 0;
        if(category.equals("Marathon")){
            imageRes = R.drawable.marathon;
        }else if(category.equals("Festival")){
            imageRes = R.drawable.festival;
        }else if(category.equals("Football")){
            imageRes = R.drawable.football;
        }else if(category.equals("Exhibition")){
            imageRes = R.drawable.exhibition;
        }else if(category.equals("Stand-Up & Theater")){
            imageRes = R.drawable.standup;
        }else if(category.equals("Festival")){
            imageRes = R.drawable.festival;
        }else if(category.equals("Concert")){
            imageRes = R.drawable.concert;
        }else if(category.equals("Party")) {
            imageRes = R.drawable.party;
        }

        return imageRes;
    }
}
