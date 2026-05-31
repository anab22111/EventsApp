package com.example.eventsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InterestedEventsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView listView;
    private TextView emptyView;
    private EventAdapter adapter;
    private String username;

    private ArrayList<Event> listInterestedEvents;
    private dbHelper helper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interested_events);

        listView = findViewById(R.id.listInterested);
        emptyView = findViewById(R.id.tvInterestedEvents);

        helper = new dbHelper(this);

        username = getIntent().getStringExtra("username");

        // make list and connect to adapter
        listInterestedEvents = new ArrayList<>();
        adapter = new EventAdapter(this, listInterestedEvents);

        // connect adapter and listView
        listView.setAdapter(adapter);

        // set empty view to list
        listView.setEmptyView(emptyView);

        // set listener
        listView.setOnItemClickListener(this);

        // get data from server
        getInterestedEventsFromServer();

    }

    private void getInterestedEventsFromServer(){
        String userId = getServerUserId(username);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpHelper httpHelper = new HttpHelper();
                // make url for users event
                String url = "http://192.168.0.7:3000/attendance/" + userId;

                String errorText = null;
                ArrayList<Event> serverEvents = new ArrayList<>();

                try {
                    JSONArray jsonArray = httpHelper.getJSONArrayFromUrl(url);  // get list of events

                    if (jsonArray != null) {
                        // go through array of JSONObjects
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject attendanceObj = jsonArray.getJSONObject(i);  // get JSONObject

                            // server returns object that has fields .commitment and .event
                            String commitment = attendanceObj.getString("commitment");

                            // check if commitment of an event is ZAINTERESOVAN
                            if (commitment.equals("ZAINTERESOVAN")) {
                                // get name of the event
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
                            Toast.makeText(InterestedEventsActivity.this, finalErrorText, Toast.LENGTH_LONG).show();
                            return;
                        }
                        listInterestedEvents.clear();
                        listInterestedEvents.addAll(finalEvents);
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
        thread.start();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // create intent
        Intent intent = new Intent(InterestedEventsActivity.this,
                EventDetailsActivity.class);

        // get event to transfer to next activity
        Event event = (Event) parent.getItemAtPosition(position);

        Bundle bundle = new Bundle();
        bundle.putString("nameOfEvent", event.getName());
        bundle.putString("username", username);

        intent.putExtras(bundle);

        // start Activity
        startActivity(intent);
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
        getInterestedEventsFromServer();
    }
}