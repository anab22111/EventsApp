package com.example.eventsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class EventsActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView tvWelcome,tvUsername;
    private Button btnEvents, btnMyEvents, btnFriends;
    String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        //preuzimanje podataka koji su preneti iz prethodnog activity-a
        Bundle bundle = getIntent().getExtras();    //uzimanje reference bundle-a koji je prosledjen


        String username = bundle.getString("username");
        email = bundle.getString("email");  //ako je prosledjen samo username email ce biti null

        tvUsername = findViewById(R.id.username);
        tvUsername.setText(username);
        tvWelcome = findViewById(R.id.welcome);

        btnEvents = findViewById(R.id.events);
        btnMyEvents = findViewById(R.id.myEvents);
        btnFriends = findViewById(R.id.friends);

        //load EventsFragment at the beggining
        getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragmentContanier, new EventsFragment())
                                .commit();

        btnEvents.setOnClickListener(this);
        btnMyEvents.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.events){    //load fragment eventsFragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContanier, new EventsFragment())
                    .commit();
        }else if(view.getId() == R.id.myEvents){    //load fragment myEventsFragment

            //sending data to myEventsFragment
            String username = tvUsername.getText().toString();

            Bundle bundle = new Bundle();
            bundle.putString("username",username);
            bundle.putString("email", email);

            MyEventsFragment myEventsFragment = new MyEventsFragment();
            myEventsFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContanier, myEventsFragment)
                    .commit();
        }
    }
}