package com.example.eventsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class AttendingEventsActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView tvHeaderUpcoming, tvHeaderPast, tvNoEvents;
    private LinearLayout containerPast, containerUpcoming;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attending_events);

        tvHeaderPast = findViewById(R.id.tvHeaderPast);
        tvHeaderUpcoming = findViewById(R.id.tvHeaderUpcoming);
        tvNoEvents = findViewById(R.id.tvNoAttendEvents);
        containerPast = findViewById(R.id.containerPassed);
        containerUpcoming = findViewById(R.id.containerUpcoming);

        getAppearance();
    }

    public void getAppearance(){
        // refresh everything
        containerUpcoming.removeAllViews();
        containerPast.removeAllViews();

        boolean hasUpcoming = false;
        boolean hasPast = false;

        dbHelper helper = new dbHelper(this);

        // get username
        username = getIntent().getStringExtra("username");

        // get attending list
        ArrayList<Event> attendingEventsList = helper.getEventsByCommitment(username, "ATTENDING");

        // create adapter for attendingEvents list
        EventAdapter adapter = new EventAdapter(this, attendingEventsList);

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

}