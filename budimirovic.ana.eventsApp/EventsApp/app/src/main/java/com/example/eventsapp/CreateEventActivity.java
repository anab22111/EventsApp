package com.example.eventsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateEventActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etEventName, etDescription, etLocation, etDateTime, etCapacity;
    private Spinner spinner;
    private CheckBox checkbox;
    private Button btnCreateEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        etEventName = findViewById(R.id.etEventName);
        etDescription = findViewById(R.id.etDescription);
        etLocation = findViewById(R.id.etLocation);
        etDateTime = findViewById(R.id.etDateTime);
        etCapacity = findViewById(R.id.etCapacity);
        spinner = findViewById(R.id.spinner);
        checkbox = findViewById(R.id.checkBox);
        btnCreateEvent = findViewById(R.id.btnCreateEvent);

        btnCreateEvent.setOnClickListener(this);
        checkbox.setOnCheckedChangeListener(null);

        // make adapter for the list of categories
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        // add categories
        adapter.add("Marathon");
        adapter.add("Party");
        adapter.add("Exhibition");
        adapter.add("Football");
        adapter.add("Stand-Up & Theater");
        adapter.add("Festival");
        adapter.add("Concert");

        // connect adapter and spinner
        spinner.setAdapter(adapter);

        // set listener for checkbox
        checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                etCapacity.setVisibility(View.VISIBLE); // show if checked
            } else {
                etCapacity.setVisibility(View.GONE);    // hide if not checked
                etCapacity.setText("");                 // refresh for next time
            }
        });
    }

    @Override
    public void onClick(View view) {
        // when user clicks to create new event check all the fields
        if(view.getId() == R.id.btnCreateEvent){
            String name = etEventName.getText().toString();
            String dateTime = etDateTime.getText().toString();
            String location = etLocation.getText().toString();
            String c = etCapacity.getText().toString();
            String description = etDescription.getText().toString();
            String category = spinner.getSelectedItem().toString();
            int capacity = 0;


            // if any of these fields are empty send toast message and don't create event
            if(name.isEmpty() || dateTime.isEmpty() || location.isEmpty()){
                Toast.makeText(CreateEventActivity.this, "Fill in all the mandatory fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            // check if checkbox is checked and capacity is correct
            if (checkbox.isChecked()) {
                if (c.isEmpty()) {
                    Toast.makeText(this, "Capacity cannot be empty for promoted events.", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    capacity = Integer.parseInt(c);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Please enter a valid number for capacity.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(capacity <= 0){
                    Toast.makeText(this, "Capacity must be greater than 0.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            // all fields field, check if format of DateTime is correct

            // create format for date and time
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy. HH:mm", Locale.getDefault());

            sdf.setLenient(false);   // false, so that java doesn't correct the input automatically

            try {
                Date date = sdf.parse(dateTime);    // try to parse dateTime with created format
                // if it passes its correct
            } catch (ParseException e) {
                // if there is an exception, input is wrong
                Toast.makeText(this, "Wrong date and time format. Try again.", Toast.LENGTH_SHORT).show();
                return;
            }

            // get correct image for the category
            int image = getImageRes(category);

            // check if event is promoted
            boolean isPromoted = checkbox.isChecked() ? true : false;

            // post to global database

            // create json for server
            JSONObject data = new JSONObject();
            try {
                data.put("name", name);
                data.put("description", description);
                data.put("location", location);
                data.put("eventTime", dateTime);
                data.put("category", category);
                data.put("promoted", isPromoted);
                data.put("capacity", capacity);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            final int cap = capacity;


            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String url = "/events";

                    HttpHelper httpHelper = new HttpHelper();   // get http helper

                    JSONObject serverResponse = null;
                    String errorText = null;

                    try {
                        serverResponse = httpHelper.postJSONObjectFromURL(url, data, "POST");
                    } catch (IOException e) {
                        e.printStackTrace();    // if there is no internet or server is off
                        errorText = "Server unreachable.";
                    } catch (JSONException e) {
                        e.printStackTrace();      // if server returns something that's not JSON
                        errorText = "Server didn't return JSON.";
                    }catch (Exception e) {
                        e.printStackTrace();
                        errorText = "Error " + e.getMessage();
                    }

                    final JSONObject response = serverResponse;
                    final String finalErrorText = errorText;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // check if there was an error with the server
                            if (finalErrorText != null) {
                                Toast.makeText(CreateEventActivity.this, finalErrorText, Toast.LENGTH_LONG).show();
                                return;        // print error and exit
                            }

                            if(response != null){
                                // get response code
                                int statusCode = response.optInt("http_status_code",0);

                                // check if creating newEvent was a success
                                if(statusCode == 200){       // success
                                    Toast.makeText(CreateEventActivity.this, "Event successfully created.", Toast.LENGTH_SHORT).show();

                                    // get event id in server
                                    String serverId = response.optString("_id");

                                    // add to local database
                                    addToLocalDB(name, description, location, dateTime, category, isPromoted, cap, serverId);
                                }else{
                                    Toast.makeText(CreateEventActivity.this, "Creating new event failed. Status: " + statusCode, Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(CreateEventActivity.this, "Unknown error from server", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
            thread.start();
        }
    }
    private void addToLocalDB(String name, String description, String location, String dateTime, String category, boolean promoted, int capacity, String serverId){
        // make helper and get database
        dbHelper helper = new dbHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();

        int isPromoted = 0;

        if (promoted) isPromoted = 1;
        else isPromoted = 0;

        values.put("name", name);
        values.put("description", description);
        values.put("location", location);
        values.put("dateTime", dateTime);
        values.put("category", category);
        values.put("promoted", isPromoted);
        values.put("capacity", capacity);
        values.put("server_id", serverId);

        db.insert("events", null, values);

        finish();
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
        }else if(category.equals("Concert")){
            imageRes = R.drawable.concert;
        }else if(category.equals("Party")){
            imageRes = R.drawable.party;
        }else if(category.equals("Special")){
            imageRes = R.drawable.limited;
        }

        return imageRes;
     }
}