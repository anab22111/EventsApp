package com.example.eventsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class EventsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        //preuzimanje podataka koji su preneti iz prethodnog activity-a
        Bundle bundle = getIntent().getExtras();    //uzimanje reference bundle-a koji je prosledjen


        String username = bundle.getString("username");
        String email = bundle.getString("email");  //ako je prosledjen samo username email ce biti null





    }
}