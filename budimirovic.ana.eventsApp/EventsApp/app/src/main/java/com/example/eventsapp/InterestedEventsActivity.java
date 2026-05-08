package com.example.eventsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class InterestedEventsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView listView;
    private TextView emptyView;
    private EventAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interested_events);

        listView = findViewById(R.id.listInterested);
        emptyView = findViewById(R.id.tvInterestedEvents);

        // get data - list interestedEvents for AppDAta
        ArrayList<Event> listInterestedEvents = new ArrayList<>(AppData.interestedEvents);

        // sort list
        listInterestedEvents.sort((e1, e2) -> Boolean.compare(e2.isPromoted(), e1.isPromoted()));

        // make adapter for real data
        adapter = new EventAdapter(this, listInterestedEvents);

        // connect adapter and listView
        listView.setAdapter(adapter);

        // set empty view to list
        listView.setEmptyView(emptyView);

        // set listener
        listView.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // create intent
        Intent intent = new Intent(InterestedEventsActivity.this,
                EventDetailsActivity.class);

        // get event to transfer to next activity
        Event event = (Event) parent.getItemAtPosition(position);

        // send only name
        intent.putExtra("nameOfEvent", event.getName());

        // start Activity
        startActivity(intent);
    }
}