package com.example.eventsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class EventDetailsActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView tvName, tvCategory, tvDescription, tvLocation, tvDateTime, tvRating, tvFreeSeats;
    private Button btnInterested, btnAttending;
    private ImageView image;

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
        image = findViewById(R.id.image);

        tvName.setText(event.getName());
        tvCategory.setText(event.getCategory());
        tvDescription.setText(event.getDescription());
        tvLocation.setText(event.getLocation());
        tvRating.setText("No rating yet");
        tvDateTime.setText(event.getDateTime());

        // get correct image for the category
        int imageRes = getImageRes(event.getCategory());
        image.setImageResource(imageRes);

        if(event.isPromoted()){
            tvFreeSeats.setText("Slobodnih mesta: " + event.getCapacity()+"/"+event.getCapacity());
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

        if(view.getId() == R.id.btnInterested){
            // get event from tag
            Event event = AppData.findByName(btnInterested.getTag().toString());

            AppData.interestedEvents.add(event);
            Toast.makeText(EventDetailsActivity.this, "Added to interested.", Toast.LENGTH_SHORT).show();
        }else if(view.getId() == R.id.btnAttending){
            // get event from tag
            Event event = AppData.findByName(btnInterested.getTag().toString());

            AppData.attendingEvents.add(event);
            Toast.makeText(EventDetailsActivity.this, "You have registered for the event.", Toast.LENGTH_SHORT).show();

        }

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
        }

        return imageRes;
    }
}