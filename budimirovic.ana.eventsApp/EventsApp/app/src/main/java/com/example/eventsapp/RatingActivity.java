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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        // get name of event
        String name = getIntent().getStringExtra("nameOfEvent");

        tvName = findViewById(R.id.tvName);
        tvName.setText(name);   // set name of event

        star1 = findViewById(R.id.star1);
        star2 = findViewById(R.id.star2);
        star3 = findViewById(R.id.star3);
        star4 = findViewById(R.id.star4);
        star5 = findViewById(R.id.star5);

        btnConfirm = findViewById(R.id.btnConfirmRating);
        btnConfirm.setTag(name);   // set tag to transfer name

        btnConfirm.setOnClickListener(this);
        star1.setOnCheckedChangeListener(this);
        star2.setOnCheckedChangeListener(this);
        star3.setOnCheckedChangeListener(this);
        star4.setOnCheckedChangeListener(this);
        star5.setOnCheckedChangeListener(this);

    }

    @Override
    public void onClick(View view) {
        // get name from Tag
        String name = view.getTag().toString();

        // get event with that name from AppData
        Event event = AppData.findByName(name);

        if(this.currRating == 0){
            Toast.makeText(this, "Rate the event before confirming.", Toast.LENGTH_SHORT).show();
        }else{
            // update AverageRating and rating count
            double currentAvg = event.getAverageRating();
            int count = event.getRatingCount();

            // get new average rating
            double newAvg = ((currentAvg * count) + currRating) / (count + 1);

            // 3. save new average rating and increase rating count
            event.setAverageRating(newAvg);
            event.setRatingCount(count + 1);
            Toast.makeText(this, "Rating saved.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        int rating = 0;
        int id = buttonView.getId();

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