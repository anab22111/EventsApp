package com.example.eventsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

public class RatingActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener{
    private TextView tvRateEvent, tvName;
    private Button btnConfirm;
    private CheckBox star1, star2, star3, star4, star5;

    private double currRating;
    private String eventName, username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        Bundle bundle = getIntent().getExtras();
        // get name of event
        username = bundle.getString("username");
        eventName = bundle.getString("nameOfEvent");

        tvName = findViewById(R.id.tvName);
        tvName.setText(eventName);   // set name of event

        star1 = findViewById(R.id.star1);
        star2 = findViewById(R.id.star2);
        star3 = findViewById(R.id.star3);
        star4 = findViewById(R.id.star4);
        star5 = findViewById(R.id.star5);

        btnConfirm = findViewById(R.id.btnConfirmRating);

        btnConfirm.setOnClickListener(this);
        star1.setOnCheckedChangeListener(this);
        star2.setOnCheckedChangeListener(this);
        star3.setOnCheckedChangeListener(this);
        star4.setOnCheckedChangeListener(this);
        star5.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View view) {

        // get event with that name from database
        dbHelper helper = new dbHelper(this);
        Event event = helper.getEventByName(eventName);

        if(this.currRating == 0){
            Toast.makeText(this, "Rate the event before confirming.", Toast.LENGTH_SHORT).show();
        }else{
            // update AverageRating and rating count
            double currentAvg = event.getAverageRating();
            int count = event.getRatingCount();

            // get new average rating
            double newAvg = ((currentAvg * count) + currRating) / (count + 1);

            // save new average rating and increase rating count
            event.setAverageRating(newAvg);
            event.setRatingCount(count + 1);
            Toast.makeText(this, "Rating saved.", Toast.LENGTH_SHORT).show();

            // update ratings table
            helper.insertRating(username, eventName, (int)currRating, newAvg);
            finish();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        int id = buttonView.getId();
        int rating = 0;

        // check what star was clicked
        if (id == R.id.star1) rating = 1;
        else if (id == R.id.star2) rating = 2;
        else if (id == R.id.star3) rating = 3;
        else if (id == R.id.star4) rating = 4;
        else if (id == R.id.star5) rating = 5;

        updateStars(rating);     // update stars to protect the program from invalid input
    }

    public void updateStars(int rating){
        // shut down listeners so that they don't trigger while checking stars
        star1.setOnCheckedChangeListener(null);
        star2.setOnCheckedChangeListener(null);
        star3.setOnCheckedChangeListener(null);
        star4.setOnCheckedChangeListener(null);
        star5.setOnCheckedChangeListener(null);

        // set checks
        star1.setChecked(rating >= 1);
        star2.setChecked(rating >= 2);
        star3.setChecked(rating >= 3);
        star4.setChecked(rating >= 4);
        star5.setChecked(rating >= 5);

        // bring back listeners
        star1.setOnCheckedChangeListener(this);
        star2.setOnCheckedChangeListener(this);
        star3.setOnCheckedChangeListener(this);
        star4.setOnCheckedChangeListener(this);
        star5.setOnCheckedChangeListener(this);

        // set rating
        this.currRating = rating;


    }
}