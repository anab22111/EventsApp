package com.example.eventsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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

                capacity = Integer.parseInt(c);   // convert string to int

                if(capacity <= 0){
                    Toast.makeText(this, "Capacity must be greater than 0.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            // TO DO ?????
            // all fields field, check if format of DateTime is correct


            String nesto = "nesto";  // dok ne zavrsim spinner  !!!!!!!!!!!!!!!!!!!!!!!

            // create event
            if(checkbox.isChecked()){    // create promoted event
                Event event = EventFactory.createPromotedEvent(name, description, location, dateTime, nesto, R.drawable.marathon_runner, capacity);
            }else{     // create regular event
                Event event = EventFactory.createRegularEvent(name, description, location, dateTime, nesto, R.drawable.marathon_runner);
            }

            // add EVent to list  TO DO !!!!!!!!!!!!!!!!!!!!!!

            Toast.makeText(this, "Event successfully created.", Toast.LENGTH_SHORT).show();
            finish();



        }
    }


}