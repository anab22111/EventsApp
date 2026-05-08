package com.example.eventsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

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

        // set istener for checkbox
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



            // if any of these fields are empty send toast message and dont create event
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

            // TO DO ?????
            // all fields field, check if format of DateTime is correct

            // get correct image for the category
            int image = getImageRes(category);

            // create event
            Event event;
            if(checkbox.isChecked()){    // create promoted event
                event = EventFactory.createPromotedEvent(name, description, location, dateTime, category, image, capacity);
            }else{     // create regular event
                event = EventFactory.createRegularEvent(name, description, location, dateTime, category, image);
            }

            AppData.allEvents.add(event);    // add Event to list

            Toast.makeText(this, "Event successfully created.", Toast.LENGTH_SHORT).show();
            finish();

        }
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
        }else if(category.equals("Festival")){
            imageRes = R.drawable.festival;
        }else if(category.equals("Concert")){
            imageRes = R.drawable.concert;
        }

        return imageRes;
     }


}