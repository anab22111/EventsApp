package com.example.eventsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class EventsActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView tvWelcome,tvUsername, tvUpcominEvents;
    private Button events, myEvents, friends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        //preuzimanje podataka koji su preneti iz prethodnog activity-a
        Bundle bundle = getIntent().getExtras();    //uzimanje reference bundle-a koji je prosledjen


        String username = bundle.getString("username");
        String email = bundle.getString("email");  //ako je prosledjen samo username email ce biti null

        tvUsername = findViewById(R.id.username);
        tvUsername.setText(username);
        tvWelcome = findViewById(R.id.welcome);
        tvUpcominEvents = findViewById(R.id.tvUpcomingEvents);

        events = findViewById(R.id.events);
        myEvents = findViewById(R.id.myEvents);
        friends = findViewById(R.id.friends);
        friends.setEnabled(false);

        events.setOnClickListener(this);
        myEvents.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

    }
}