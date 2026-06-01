package com.example.eventsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class RatingActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener{
    private TextView tvRateEvent, tvName;
    private Button btnConfirm;
    private CheckBox star1, star2, star3, star4, star5;

    private int currRating;
    private String eventName, username;
    private dbHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        helper = new dbHelper(this);

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
            return;
        }
        // user rated the event

        // get event and user id from server
        String userId = getServerUserId(username);
        String eventId = getServerEventId(eventName);

        // make json for server
        final JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("userId", userId);
            jsonData.put("eventId", eventId);
            jsonData.put("rating", (int) currRating);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Thread thread= new Thread(new Runnable() {
            @Override
            public void run() {
                HttpHelper httpHelper = new HttpHelper();
                String url = "http://10.194.239.97:3000/ratings";

                JSONObject serverResponse = null;
                String errorText = null;

                try {
                    serverResponse = httpHelper.postJSONObjectFromURL(url, jsonData, "POST");
                } catch (IOException e) {
                    e.printStackTrace();
                    errorText = "Server unreachable.";
                } catch (JSONException e) {
                    e.printStackTrace();
                    errorText = "Server didn't return valid JSON.";
                }

                final JSONObject response = serverResponse;
                final String finalErrorText = errorText;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (finalErrorText != null) {
                            Toast.makeText(RatingActivity.this, finalErrorText, Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (response != null) {    // server returned a response

                            int statusCode = response.optInt("http_status_code", 0);

                            // check status code if it was successful
                            if(statusCode == 200){       // success

                                // get new average rating and number of ratings
                                int numOfRatings = response.optInt("numberOfRatings");
                                double avgRating = response.optDouble("avgRating");

                                ContentValues cv = new ContentValues();
                                cv.put("avgRating", avgRating);
                                cv.put("numberOfRatings", numOfRatings);

                                // update local db events
                                SQLiteDatabase db = helper.getWritableDatabase();
                                db.update("events", cv, "name = ?", new String[]{eventName});

                                // update local database ratings
                                helper.insertRating(username, eventName,currRating, avgRating);

                                Toast.makeText(RatingActivity.this, "Rating saved.", Toast.LENGTH_SHORT).show();
                                finish();

                            }else{
                                String errorMessage = response.optString("message");
                                Toast.makeText(RatingActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

            }
        });
        thread.start();
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

    private String getServerEventId(String name){
        // get local database
        SQLiteDatabase db = helper.getReadableDatabase();
        // make cursor to get event id from table
        Cursor cursor = db.rawQuery("SELECT server_id FROM events WHERE name = ?", new String[]{name});

        String serverId = "";
        if(cursor.moveToFirst()){
            serverId = cursor.getString(cursor.getColumnIndexOrThrow("server_id"));
        }

        cursor.close();
        return serverId;
    }

    private String getServerUserId(String name){
        // get local database
        SQLiteDatabase db = helper.getReadableDatabase();
        // make cursor to get event id from table
        Cursor cursor = db.rawQuery("SELECT server_id FROM users WHERE username = ?", new String[]{name});

        String serverId = "";
        if(cursor.moveToFirst()){
            serverId = cursor.getString(cursor.getColumnIndexOrThrow("server_id"));
        }

        cursor.close();
        return serverId;

    }
}