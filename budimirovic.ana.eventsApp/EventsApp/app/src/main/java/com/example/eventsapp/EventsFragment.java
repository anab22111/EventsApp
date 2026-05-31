package com.example.eventsapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

///**
// * A simple {@link Fragment} subclass.
// * Use the {@link EventsFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class EventsFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener{
    private Button btnFestival, btnFootball, btnExhibition, btnMarathon, btnConcert, btnParty, btnStandUpTheater, btnAll, btnAddEvent;
    private ListView list;
    private TextView emptyView;
    private ArrayList<Event> events;
    private EventAdapter adapter;
    private dbHelper dbHelper;
    private String username;
    private boolean isAdmin;

    private String currentCategory = "All";   // used to remember what button was clicked(what category)
    // initial button is All

    public EventsFragment(){}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_events, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle1 = getArguments();
        username = bundle1.getString("username");
        isAdmin = bundle1.getBoolean("isAdmin");

        // make list of events
        events = new ArrayList<>();

        // make adapter for list
        adapter = new EventAdapter(getContext(), events);    // have to use getContext bc fragment isn't an Activity

        list = view.findViewById(R.id.list);

        // connect list to adapter
        list.setAdapter(adapter);

        // get list of events from server
        getEvents(currentCategory);                 // getEvents updates adapter

        // initiate dbHelper
        dbHelper = new dbHelper(getContext());


        // set emptyView for list
        emptyView = view.findViewById(R.id.tvNoUpcomingEvents);
        list.setEmptyView(emptyView);

        // just findViewById doesn't work because fragment doesn't have that method, therefore need to use view
        // get all elements
        btnExhibition = view.findViewById(R.id.btnExhibition);
        btnFootball = view.findViewById(R.id.btnFootball);
        btnMarathon = view.findViewById(R.id.btnMarathon);
        btnFestival = view.findViewById(R.id.btnFestival);
        btnParty = view.findViewById(R.id.btnParty);
        btnStandUpTheater = view.findViewById(R.id.btnStandUpTheater);
        btnAll = view.findViewById(R.id.btnAll);
        btnAddEvent = view.findViewById(R.id.btnAddEvent);
        btnConcert = view.findViewById(R.id.btnConcert);

        // at the beginning all button is "clicked" so it is different color
        btnAll.setBackgroundColor(getResources().getColor(R.color.plum));

        // check if user is admin
        if(isAdmin == true){
            btnAddEvent.setVisibility(View.VISIBLE);
        }else{
            btnAddEvent.setVisibility(View.GONE);
        }

        list.setOnItemClickListener(this);
        btnExhibition.setOnClickListener(this);
        btnFootball.setOnClickListener(this);
        btnMarathon.setOnClickListener(this);
        btnFestival.setOnClickListener(this);
        btnParty.setOnClickListener(this);
        btnStandUpTheater.setOnClickListener(this);
        btnAll.setOnClickListener(this);
        btnAddEvent.setOnClickListener(this);
        btnConcert.setOnClickListener(this);
    }


    // parent - the AdapterView where the click happened
    // view - The view within the AdapterView that was clicked (this will be a view provided by the adapter)
    // position - position of the view in adapter
    // id - The row id of the item that was clicked.
    // id and position usually the same
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        // create intent
        Intent intent = new Intent(getContext(),
                EventDetailsActivity.class);

        // get event to transfer to next activity
        Event event = (Event) parent.getItemAtPosition(position);
        Bundle bundle1 = getArguments();
        String user = bundle1.getString("username");

        Bundle bundle = new Bundle();
        bundle.putString("nameOfEvent", event.getName());           // send name of event
        bundle.putString("username", user); // send username
        intent.putExtras(bundle);

        // start Activity
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.btnExhibition){
            // if a button is clicked reset the color of all buttons
            resetColors();
            btnExhibition.setBackgroundColor(getResources().getColor(R.color.plum));
            currentCategory = "Exhibition";

        }else if(view.getId() == R.id.btnMarathon){
            // if a button is clicked reset the color of all buttons
            resetColors();
            btnMarathon.setBackgroundColor(getResources().getColor(R.color.plum));
            currentCategory = "Marathon";

        }else if(view.getId() == R.id.btnFootball){
            // if a button is clicked reset the color of all buttons
            resetColors();
            btnFootball.setBackgroundColor(getResources().getColor(R.color.plum));
            currentCategory = "Football";

        }else if(view.getId() == R.id.btnFestival){
            // if a button is clicked reset the color of all buttons
            resetColors();
            btnFestival.setBackgroundColor(getResources().getColor(R.color.plum));
            currentCategory = "Festival";

        }else if(view.getId() == R.id.btnParty){
            // if a button is clicked reset the color of all buttons
            resetColors();
            btnParty.setBackgroundColor(getResources().getColor(R.color.plum));
            currentCategory = "Party";

        }else if(view.getId() == R.id.btnStandUpTheater){
            // if a button is clicked reset the color of all buttons
            resetColors();
            btnStandUpTheater.setBackgroundColor(getResources().getColor(R.color.plum));
            currentCategory = "Stand-Up & Theater";

        }else if(view.getId() == R.id.btnAll){
            // if a button is clicked reset the color of all buttons
            resetColors();
            btnAll.setBackgroundColor(getResources().getColor(R.color.plum));
            currentCategory = "All";

        }else if(view.getId() == R.id.btnConcert){
            // if a button is clicked reset the color of all buttons
            resetColors();
            btnConcert.setBackgroundColor(getResources().getColor(R.color.plum));
            currentCategory = "Concert";

        }else if(view.getId() == R.id.btnAddEvent){
            // go to createEvent activity
            Intent intent = new Intent(getActivity(), CreateEventActivity.class);
            startActivity(intent);
            return; // exit
        }

        getEvents(currentCategory);   // update adapter

    }

    public void resetColors(){
        btnExhibition.setBackgroundColor(getResources().getColor(R.color.purple_500));
        btnFootball.setBackgroundColor(getResources().getColor(R.color.purple_500));
        btnMarathon.setBackgroundColor(getResources().getColor(R.color.purple_500));
        btnFestival.setBackgroundColor(getResources().getColor(R.color.purple_500));
        btnParty.setBackgroundColor(getResources().getColor(R.color.purple_500));
        btnStandUpTheater.setBackgroundColor(getResources().getColor(R.color.purple_500));
        btnAll.setBackgroundColor(getResources().getColor(R.color.purple_500));
        btnAddEvent.setBackgroundColor(getResources().getColor(R.color.purple_500));
        btnConcert.setBackgroundColor(getResources().getColor(R.color.purple_500));
    }

    private void getEvents(String category){
        // make http helper
        HttpHelper httpHelper = new HttpHelper();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                String originalUrl = "http://192.168.0.7:3000/events";    // url for events table - computer ip address:port/events
                String url = originalUrl;
                //String url = "http://10.0.2.2:3000/events";

                if(!category.equals("All")){    // if category isn't ALL make new url
                    try {
                        url = "http://192.168.0.7:3000/events/" + java.net.URLEncoder.encode(category, "UTF-8");  // encode bc category Theater & StandUp has spaces and &
                    } catch (java.io.UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }

                String errorText = null;
                ArrayList<Event> serverEvents = new ArrayList<>();

                // try to get events from server
                try {
                    JSONArray JSONevents = httpHelper.getJSONArrayFromUrl(url);    // server returns a JSONArray

                    if(JSONevents != null){     // check if server returned events

                        // delete all events from local database so it's not duplicated
                        //dbHelper.getWritableDatabase().delete("events", null, null);

                        // go through all elements of JSONArray
                        for(int i = 0; i < JSONevents.length(); i++){
                            JSONObject JSONEvent = JSONevents.getJSONObject(i);

                            boolean isPromoted = JSONEvent.optBoolean("promoted");
                            String name = JSONEvent.getString("name");
                            String description = JSONEvent.getString("description");
                            String location = JSONEvent.getString("location");
                            String eventTime = JSONEvent.getString("eventTime");
                            String category = JSONEvent.getString("category");
                            String id = JSONEvent.getString("_id");
                            int capacity = JSONEvent.optInt("capacity", 0);
                            int imageRes = getImageRes(category);
                            int numberOfAttendees = JSONEvent.optInt("numberOfAttendees");

                            Event event;
                            // if promoted make promoted event, otherwise make regular event
                            if(isPromoted){
                                event = EventFactory.createPromotedEvent(name,description,location,eventTime,category, imageRes ,capacity);
                            }else{
                                event = EventFactory.createRegularEvent(name,description,location,eventTime,category, imageRes);
                            }
                            serverEvents.add(event);           // add to list of events
                            event.setNumberOfAttendees(numberOfAttendees);

                            updateLocalDatabase(name,description,location,eventTime,category, imageRes ,capacity, isPromoted, id, numberOfAttendees);

                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();    // if there is no internet or server is off
                    errorText = "Server unreachable.";
                } catch (JSONException e) {
                    e.printStackTrace();
                    errorText = "Error " + e.getMessage();
                }

                // make new list to store events by promotion
                ArrayList<Event> sortedEvents = new ArrayList<>();

                // add promoted events first
                for (Event e : serverEvents) {
                    if (e.isPromoted()) {
                        sortedEvents.add(e);
                    }
                }

               // then add regular events
                for (Event e : serverEvents) {
                    if (!e.isPromoted()) {
                        sortedEvents.add(e);
                    }
                }
                serverEvents = sortedEvents;

                // update ui when finished
                final ArrayList<Event> finalReadyList = serverEvents;
                final String finalErrorText = errorText;

                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (finalErrorText != null) {
                                Toast.makeText(getContext(), finalErrorText, Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // there were no errors
                            events.clear();                  // clear global events list
                            events.addAll(finalReadyList);      // add all events from server
                            adapter.notifyDataSetChanged();     // update adapter
                        }
                    });
                }
            }
        });
        thread.start();
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
        }else if(category.equals("Concert")){
            imageRes = R.drawable.concert;
        }else if(category.equals("Party")){
            imageRes = R.drawable.party;
        }

        return imageRes;
    }

    private void updateLocalDatabase(String name,String description, String location,String eventTime,String category,int imageRes ,int capacity, boolean isPromoted, String id, int numberOfAttendees){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int promoted;
        if(isPromoted) promoted = 1;
        else promoted = 0;

        android.content.ContentValues values = new android.content.ContentValues();
        values.put("name", name);
        values.put("server_id", id);
        values.put("description", description);
        values.put("location", location);
        values.put("dateTime", eventTime);
        values.put("category", category);
        values.put("promoted",  promoted);
        values.put("capacity", capacity);
        values.put("numberOfAttendees", numberOfAttendees);

        //check if event exists in local db
        Cursor cursor = db.rawQuery("SELECT * FROM events WHERE name = ?", new String[]{name});

        if (cursor.moveToFirst()) {
            // if event exist update it
            db.update("events", values, "name = ?", new String[]{name});
        } else {
            // if not insert event
            db.insert("events", null, values);
        }
        cursor.close();
    }

    @Override
    public void onResume() {   // when back to fragment refresh the list - needed when coming back from CreateEventActivity
        super.onResume();
        // check to see if adapter exists
        if (adapter != null) {
           getEvents(currentCategory);
        }
    }

}