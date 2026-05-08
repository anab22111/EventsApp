package com.example.eventsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class RatingActivity extends AppCompatActivity {
    private TextView tvRateEvent, tvName;
    private Button btnConfirm;
    private CheckBox star1, star2, star3, star4, star5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        // get name of event
        String name = getIntent().getStringExtra("nameOfEvent");

        tvRateEvent = findViewById(R.id.tvRate);
        tvRateEvent.setText(name);   // set name of event
        


    }
}