package com.example.eventsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteDatabase;
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
    private String username;
    private dbHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        Bundle bundle = getIntent().getExtras();

        String eventName = bundle.getString("nameOfEvent");
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

        // get FreeSeats for event
        int capacity = event.getCapacity();
        int attendees = event.getNumberOfAttendees();
        int freeSeats = capacity - attendees;

        if(view.getId() == R.id.btnInterested){

            // check if user is already interested for clickedEventName
            boolean exists = helper.checkIfAttendanceExists(username, clickedEventName, "INTERESTED");
            
            if(exists){     // user already interested
                Toast.makeText(this, "Already in interested list.", Toast.LENGTH_SHORT).show();
            }else{        // add event to interested

                // check if previous commitment was ATTENDING
                boolean wasAttending = helper.checkIfAttendanceExists(username, clickedEventName, "ATTENDING");

                // try to insert attendance
                boolean success = helper.insertAttendance(username, clickedEventName, "INTERESTED");
                if (success) {
                    // if true, status changed to interested
                    Toast.makeText(this, "Added to interested.", Toast.LENGTH_SHORT).show();

                    // update appearance
                    if (wasAttending && event.isPromoted()) {
                        int newFreeSeats = freeSeats + 1;
                        tvFreeSeats.setText("Free seats: " + newFreeSeats + "/" + capacity);
                    }

                } else {
                    Toast.makeText(this, "Error updating database.", Toast.LENGTH_SHORT).show();
                }
            }

        }else if(view.getId() == R.id.btnAttending){

            // check if user is already interested for clickedEventName
            boolean exists = helper.checkIfAttendanceExists(username, clickedEventName, "ATTENDING");

            if(exists){     // user already attending
                Toast.makeText(this, "You have already registered for the event.", Toast.LENGTH_SHORT).show();
            }else{        // add event to interested

                // check if there are any free seats left
                if (event.isPromoted() && freeSeats <= 0) {                   // if event is promoted and there are no free seats don't insert new attendance and notify user
                    Toast.makeText(this, "Unfortunately, there are no free seats left.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // try to insert attendance
                boolean success = helper.insertAttendance(username, clickedEventName, "ATTENDING");
                if (success) {
                    Toast.makeText(this, "You have registered for the event.", Toast.LENGTH_SHORT).show();

                    // update free seats
                    tvFreeSeats.setText("Free seats: " + (freeSeats - 1)+"/" + event.getCapacity());

                } else {
                    Toast.makeText(this, "Error updating database.", Toast.LENGTH_SHORT).show();
                }
            }
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
        }else if(category.equals("Party")) {
            imageRes = R.drawable.party;
        }

        return imageRes;
    }
}