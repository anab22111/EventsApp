package com.example.eventsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class EventDetailsActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView tvName, tvCategory, tvDescription, tvLocation, tvDateTime, tvRating, tvFreeSeats;
    private Button btnInterested, btnAttending;
    private ImageView image;
    private String username;
    private dbHelper helper;
    private String eventName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        Bundle bundle = getIntent().getExtras();

        eventName = bundle.getString("nameOfEvent");
        username = bundle.getString("username");

        // get event form database
        helper = new dbHelper(this);

        Event event = helper.getEventByName(eventName);

        if (event == null) {
            Toast.makeText(this, "Event not found!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        tvName = findViewById(R.id.tvName);
        tvCategory = findViewById(R.id.tvCategory);
        tvDescription = findViewById(R.id.tvDescription);
        tvLocation = findViewById(R.id.tvLocation);
        tvRating = findViewById(R.id.tvRating);
        tvDateTime = findViewById(R.id.tvdateAndTime);
        tvFreeSeats = findViewById(R.id.tvFreeSeats);
        btnInterested = findViewById(R.id.btnInterested);
        btnAttending = findViewById(R.id.btnAttending);
        image = findViewById(R.id.image);

        tvName.setText(event.getName());
        tvCategory.setText(event.getCategory());
        tvDescription.setText(event.getDescription());
        tvLocation.setText(event.getLocation());

        if(event.getAverageRating() == 0){    // if there is no rating
            tvRating.setText("No rating yet");
        }else{        // else
            String ratingStr = String.format("%.1f", event.getAverageRating());
            tvRating.setText("Rating :" + ratingStr);
        }

        tvDateTime.setText(event.getDateTime());

        // get correct image for the category
        int imageRes = getImageRes(event.getCategory());
        image.setImageResource(imageRes);

        int freeSeats = event.getCapacity() - event.getNumberOfAttendees();

        if(event.isPromoted()){
            tvFreeSeats.setText("Free seats" + freeSeats + "/" + event.getCapacity());
            tvFreeSeats.setVisibility(View.VISIBLE);
        }else{
            tvFreeSeats.setVisibility(View.GONE);
        }

        btnInterested.setTag(event.getName());  // set Tags so that the event is transferred to onClick()
        btnAttending.setTag(event.getName());
        btnInterested.setOnClickListener(this);
        btnAttending.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        // get event name from tag
        String clickedEventName = view.getTag().toString();
        Event event =  helper.getEventByName(clickedEventName);

        if (event == null) return;

        String eventId = getServerEventId(clickedEventName);
        String userId = getServerUserId(username);

        String commitment = "";    // for serer commitment
        String localStatus = "";    // for local database commitment

        // check local database to see if button was already clicked and user is already interested/attending
        // if commitment stays the same no need to sen request to server
        if (view.getId() == R.id.btnInterested) {
            if (helper.checkIfAttendanceExists(username, clickedEventName, "INTERESTED")) {
                Toast.makeText(this, "Already in interested list.", Toast.LENGTH_SHORT).show();
                return;
            }
            // change commitments
            commitment = "ZAINTERESOVAN";
            localStatus = "INTERESTED";
        } else if (view.getId() == R.id.btnAttending) {
            if (helper.checkIfAttendanceExists(username, clickedEventName, "ATTENDING")) {
                Toast.makeText(this, "You have already registered for the event.", Toast.LENGTH_SHORT).show();
                return;
            }
            // change commitments
            commitment = "PRISUSTVUJE";
            localStatus = "ATTENDING";
        }

        // check previous state
        final boolean wasAttending = helper.checkIfAttendanceExists(username, clickedEventName, "ATTENDING");

        // make json for server
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("userId", userId);
            jsonData.put("eventId", eventId);
            jsonData.put("commitment", commitment);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String finalCommitment = commitment;
        final String finalLocalStatus = localStatus;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                HttpHelper httpHelper = new HttpHelper();
                String url = "http://192.168.0.7:3000/attendance";

                JSONObject serverResponse = null;
                String errorText = null;

                try {
                    serverResponse = httpHelper.postJSONObjectFromURL(url, jsonData, "POST");
                } catch (IOException e) {
                    e.printStackTrace();
                    errorText = "Server unreachable.";
                } catch (JSONException e) {
                    e.printStackTrace();
                    errorText = "Server didn't return valid JSON.";
                }

                final JSONObject response = serverResponse;
                final String finalErrorText = errorText;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (finalErrorText != null) {
                            Toast.makeText(EventDetailsActivity.this, finalErrorText, Toast.LENGTH_LONG).show();
                            return;
                        }

                        // server responded
                        if(response != null){
                            android.util.Log.d("SERVER_RESPONSE", response.toString());

                            // get status code
                            int statusCode = response.optInt("http_status_code", 200);

                            if(statusCode == 200 || statusCode == 201){    // success changing commitment

                                Toast.makeText(EventDetailsActivity.this, "Added to " + finalLocalStatus, Toast.LENGTH_SHORT).show();

                                // need to update local database - table attendance
                                // table events will be updated automatically when user returns to events fragment - updated number of attendees

                                // try to insert attendance
                                boolean success = helper.insertAttendance(username, clickedEventName, finalLocalStatus);
                                if (success) {                 // if changing commitment was successful

                                    // check if event is promoted to update seats
                                    if(event.isPromoted()){     // update gui

                                        // get previous number of attendees for event
                                        int attendees = event.getNumberOfAttendees();

                                        if (finalLocalStatus.equals("ATTENDING") && !wasAttending) {
                                            // user wants to attend
                                            attendees++;
                                        } else if (finalLocalStatus.equals("INTERESTED") && wasAttending) {
                                            // user interested but was Attending before
                                            attendees--;
                                        }
                                        int currentFreeSeats = event.getCapacity() - attendees;
                                        // update screen
                                        tvFreeSeats.setText("Free seats: " + currentFreeSeats + "/" + event.getCapacity());

                                        // update local event so that ui can function correct i user changes his mind
                                        event.setNumberOfAttendees(attendees);

                                        // update local database
                                        ContentValues cv = new ContentValues();
                                        cv.put("numberOfAttendees", attendees);
                                        helper.getWritableDatabase().update("events", cv, "name = ?", new String[]{clickedEventName});
                                    }
                                } else {
                                    Toast.makeText(EventDetailsActivity.this, "Error updating database.", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                String errorMessage = response.optString("message");
                                Toast.makeText(EventDetailsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });
        thread.start();
    }

    private String getServerEventId(String name){
        // get local database
        SQLiteDatabase db = helper.getReadableDatabase();
        // make cursor to get event id from table
        Cursor cursor = db.rawQuery("SELECT server_id FROM events WHERE name = ?", new String[]{name});

        String serverId = "";
        if(cursor.moveToFirst()){
            serverId = cursor.getString(cursor.getColumnIndexOrThrow("server_id"));
        }

        cursor.close();
        return serverId;
    }

    private String getServerUserId(String name){
        // get local database
        SQLiteDatabase db = helper.getReadableDatabase();
        // make cursor to get event id from table
        Cursor cursor = db.rawQuery("SELECT server_id FROM users WHERE username = ?", new String[]{name});

        String serverId = "";
        if(cursor.moveToFirst()){
            serverId = cursor.getString(cursor.getColumnIndexOrThrow("server_id"));
        }

        cursor.close();
        return serverId;

    }

    private int getImageRes(String category){
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