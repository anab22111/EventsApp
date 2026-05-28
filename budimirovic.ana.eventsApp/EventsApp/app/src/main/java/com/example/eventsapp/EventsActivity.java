package com.example.eventsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class EventsActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView tvWelcome,tvUsername;
    private Button btnEvents, btnMyEvents, btnFriends;
    private String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        //taking data that was transferred from the MainActivity
        Bundle bundle = getIntent().getExtras();    //taking a reference of the bundle that was forwarded

        String username = bundle.getString("username");

        tvUsername = findViewById(R.id.username);
        tvUsername.setText(username);                   //set forwarded username
        tvWelcome = findViewById(R.id.welcome);

        btnEvents = findViewById(R.id.events);
        btnMyEvents = findViewById(R.id.myEvents);
        btnFriends = findViewById(R.id.friends);

        EventsFragment EventsFragment = new EventsFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putString("username",username);
        EventsFragment.setArguments(bundle1);

        //load EventsFragment at the beginning
        getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragmentContanier, EventsFragment)
                                .commit();

        btnEvents.setOnClickListener(this);
        btnMyEvents.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.events){    //load fragment eventsFragment

            // send username to eventsFragment
            String username = tvUsername.getText().toString();

            //sending data by setting arguments for the next fragment
            EventsFragment EventsFragment = new EventsFragment();
            Bundle bundle = new Bundle();
            bundle.putString("username",username);
            EventsFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContanier, EventsFragment)
                    .commit();

        }else if(view.getId() == R.id.myEvents){    //load fragment myEventsFragment

            //sending data to myEventsFragment
            String username = tvUsername.getText().toString();

            Bundle bundle = new Bundle();
            bundle.putString("username",username);

            //sending data by setting arguments for the next fragment
            MyEventsFragment myEventsFragment = new MyEventsFragment();
            myEventsFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContanier, myEventsFragment)
                    .commit();
        }
    }
}