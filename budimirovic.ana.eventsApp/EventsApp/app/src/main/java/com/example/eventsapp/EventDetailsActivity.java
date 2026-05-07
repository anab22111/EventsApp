package com.example.eventsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class EventDetailsActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView tvName, tvCategory, tvDescription, tvLocation, tvDateTime, tvRating, tvFreeSeats;
    private Button btnInterested, btnAttending;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        String name = getIntent().getStringExtra("nameOfEvent");

        Event event = AppData.findByName(name);

        tvName = findViewById(R.id.tvName);
        tvCategory = findViewById(R.id.tvCategory);
        tvDescription = findViewById(R.id.tvDescription);
        tvLocation = findViewById(R.id.tvLocation);
        tvRating = findViewById(R.id.tvRating);
        tvDateTime = findViewById(R.id.tvdateAndTime);
        tvFreeSeats = findViewById(R.id.tvFreeSeats);
        btnInterested = findViewById(R.id.btnInterested);
        btnAttending = findViewById(R.id.btnAttending);

        tvName.setText(event.getName());
        tvCategory.setText(event.getCategory());
        tvDescription.setText(event.getDescription());
        tvLocation.setText(event.getLocation());
        tvRating.setText("No rating yet");
        tvDateTime.setText(event.getDateTime());

        if(event.isPromoted()){
            tvFreeSeats.setText("Slobodnih mesta: " + event.getCapacity()+"/"+event.getCapacity());
            tvFreeSeats.setVisibility(View.VISIBLE);
        }else{
            tvFreeSeats.setVisibility(View.GONE);
        }

        btnInterested.setOnClickListener(this);
        btnAttending.setOnClickListener(this);



    }

    @Override
    public void onClick(View view) {

    }
}