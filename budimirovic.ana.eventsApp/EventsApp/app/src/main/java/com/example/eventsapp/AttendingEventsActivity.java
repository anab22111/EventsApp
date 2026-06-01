package com.example.eventsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;

public class AttendingEventsActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView tvHeaderUpcoming, tvHeaderPast, tvNoEvents;
    private LinearLayout containerPast, containerUpcoming;
    private String username;
    private dbHelper helper;
    private ArrayList<Event> attendingEventsList;
    private EventAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attending_events);

        tvHeaderPast = findViewById(R.id.tvHeaderPast);
        tvHeaderUpcoming = findViewById(R.id.tvHeaderUpcoming);
        tvNoEvents = findViewById(R.id.tvNoAttendEvents);
        containerPast = findViewById(R.id.containerPassed);
        containerUpcoming = findViewById(R.id.containerUpcoming);

        // get username
        username = getIntent().getStringExtra("username");

        // make list to place events
        attendingEventsList = new ArrayList<>();

        // create adapter for attendingEvents list
        adapter = new EventAdapter(this, attendingEventsList);

        helper = new dbHelper(this);

        // fill list with events from server
        getAttendingEventsList();
    }



    private void getAttendingEventsList(){
        // get user id
        String userId = getServerUserId(username);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpHelper httpHelper = new HttpHelper();
                // make url for users event
                String url = "http://10.194.239.97:3000/attendance/" + userId;

                String errorText = null;
                ArrayList<Event> serverEvents = new ArrayList<>();   // make list to store events

                try {
                    JSONArray jsonArray = httpHelper.getJSONArrayFromUrl(url);  // get list of events

                    if (jsonArray != null) {
                        // go through array of JSONObjects
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject attendanceObj = jsonArray.getJSONObject(i);  // get JSONObject

                            // server returns object that has fields .commitment and .event
                            String commitment = attendanceObj.getString("commitment");

                            // check if commitment of an event is ZAINTERESOVAN
                            if (commitment.equals("PRISUSTVUJE")) {
                                // get JSONObject for event
                                JSONObject jsonEvent = attendanceObj.getJSONObject("event");

                                boolean isPromoted = jsonEvent.optBoolean("promoted");
                                String name = jsonEvent.getString("name");
                                String description = jsonEvent.getString("description");
                                String location = jsonEvent.getString("location");
                                String eventTime = jsonEvent.getString("eventTime");
                                String category = jsonEvent.getString("category");
                                String id = jsonEvent.getString("_id");
                                int capacity = jsonEvent.optInt("capacity", 0);
                                int numberOfAttendees = jsonEvent.optInt("numberOfAttendees", 0);
                                int imageRes = getImageRes(category);

                                Event event;
                                if (isPromoted) {
                                    event = EventFactory.createPromotedEvent(name, description, location, eventTime, category, imageRes, capacity);
                                } else {
                                    event = EventFactory.createRegularEvent(name, description, location, eventTime, category, imageRes);
                                }
                                event.setNumberOfAttendees(numberOfAttendees);

                                serverEvents.add(event);
                                //updateLocalDatabase(name, description, location, eventTime, category, capacity, isPromoted, id, numberOfAttendees);
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    errorText = "Server unreachable. Loading from local database...";
                } catch (JSONException e) {
                    e.printStackTrace();
                    errorText = "Data parsing error: " + e.getMessage();
                }

                // update UI
                final ArrayList<Event> finalEvents = serverEvents;
                final String finalErrorText = errorText;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (finalErrorText != null) {
                            Toast.makeText(AttendingEventsActivity.this, finalErrorText, Toast.LENGTH_LONG).show();
                            return;
                        }

                        // add list of finalEvents to attendingEventsList that is shown on UI
                        attendingEventsList.clear();
                        attendingEventsList.addAll(finalEvents);
                        adapter.notifyDataSetChanged();       // update adapter

                        // empty previous containers, refresh everything
                        containerUpcoming.removeAllViews();
                        containerPast.removeAllViews();

                        drawElements();
                    }
                });
            }
        });
        thread.start();
    }

    private String getServerUserId(String username){
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT server_id FROM users WHERE username = ?", new String[]{username});
        String serverId = "";
        if (cursor.moveToFirst()) {
            serverId = cursor.getString(cursor.getColumnIndexOrThrow("server_id"));
        }
        cursor.close();
        return serverId;
    }

    private void drawElements(){
        boolean hasUpcoming = false;
        boolean hasPast = false;

        // go through list and separate passed and upcoming events
        for (int i = 0; i < attendingEventsList.size(); i++) {
            Event e = attendingEventsList.get(i);   // get event with index i

            // get View for one row
            View row = adapter.getView(i, null, null);   // "make" view for an event from attendingEvents on index i

            // hide image, featured, color, category, freeSeats
            row.findViewById(R.id.eventImage).setVisibility(View.GONE);
            row.findViewById(R.id.tvPromoted).setVisibility(View.GONE);
            row.setBackgroundResource(android.R.color.white);
            row.findViewById(R.id.tvCategory).setVisibility(View.GONE);
            row.findViewById(R.id.tvFreeSeats).setVisibility(View.GONE);

            // check to see if event passed or not
            if (e.isPast()) {
                hasPast = true;
                containerPast.addView(row);    // add view to linearLayout

                Button btnRate = row.findViewById(R.id.btnRate);    // get button from row View
                if (btnRate != null) {                              // set to visible
                    btnRate.setVisibility(View.VISIBLE);
                    btnRate.setTag(e.getName());                    // set tag to transfer name - tag is connected to button
                    btnRate.setOnClickListener(this);               // set listener
                }
            }
            else {
                hasUpcoming = true;
                containerUpcoming.addView(row);

                Button btnRate = row.findViewById(R.id.btnRate);
                if (btnRate != null) {
                    btnRate.setVisibility(View.GONE);   // when upcoming event hide rate button
                }
            }
        }

        tvHeaderUpcoming.setVisibility(hasUpcoming ? View.VISIBLE : View.GONE);
        tvHeaderPast.setVisibility(hasPast ? View.VISIBLE : View.GONE);
        tvNoEvents.setVisibility(attendingEventsList.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnRate) {
            // from tag get name
            String eventName = (String) view.getTag();

            // get helper
            dbHelper helper = new dbHelper(this);

            // check if user already rated the event
            boolean alreadyRated = helper.checkIfRatingExists(username, eventName);

            if(alreadyRated){          // if already rated don't go to ratingActivity
                Toast.makeText(this, "You have already rated this event.", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(this, RatingActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("nameOfEvent", eventName);
            bundle.putString("username", username);

            intent.putExtras(bundle);
            startActivity(intent);
        }

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

    @Override
    protected void onResume() {
        super.onResume();
        getAttendingEventsList();
    }
}