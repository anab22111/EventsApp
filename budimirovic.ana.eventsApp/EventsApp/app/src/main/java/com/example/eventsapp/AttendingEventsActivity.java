package com.example.eventsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class AttendingEventsActivity extends AppCompatActivity {
    private TextView tvHeaderUpcoming, tvHeaderPast;
    private ListView lvPast, lvUpcoming;
    private EventAdapter adapterUpcoming, adapterPast;
    private ArrayList<Event> listUpcoming, listPast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attending_events);

        tvHeaderPast = findViewById(R.id.tvHeaderPast);
        tvHeaderUpcoming = findViewById(R.id.tvHeaderUpcoming);
        lvPast = findViewById(R.id.listPassed);
        lvUpcoming = findViewById(R.id.listUpcoming);
        listUpcoming = new ArrayList<>();
        listPast = new ArrayList<>();


        // get lists from AppData
        for(Event e : AppData.attendingEvents){    // go through attendingEvents list

            // check if the event pasesed
            if(e.isPast()){  // if it passed add to listPast
                listPast.add(e);
            }else{
                listUpcoming.add(e);  // else add to listUpcoming
            }
        }

        // make adapters
        adapterPast = new EventAdapter(this, listPast);
        adapterUpcoming = new EventAdapter(this, listUpcoming);

        lvPast.setAdapter(adapterPast);
        lvUpcoming.setAdapter(adapterUpcoming);

        // check if lists are empty
        if(listUpcoming.isEmpty()){tvHeaderUpcoming.setVisibility(View.GONE);}  // hide header
        if(listPast.isEmpty()){tvHeaderPast.setVisibility(View.GONE);}   // hide header




    }
}