package com.example.eventsapp;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

///**
// * A simple {@link Fragment} subclass.
// * Use the {@link EventsFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class EventsFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener{
    private Button btnFestival, btnFootball, btnExhibition, btnMarathon, btnConcert, btnParty, btnStandUpTheater, btnAll, btnAddEvent;
    private ListView list;
    private TextView emptyView;
    private ArrayList<Event> events;
    private EventAdapter adapter;
    private dbHelper dbHelper;
    private String username;

    private String currentCategory = "All";   // used to remember what button was clicked(what category)
    // initial button is All

    public EventsFragment(){}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_events, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        // make db helper
        dbHelper = new dbHelper(getContext());
        events = new ArrayList<>(dbHelper.getSortedEvents());    // get list of events form dbHelper

        // make adapter for list
        adapter = new EventAdapter(getContext(), events);    // have to use getContext bc fragment isn't an Activity

        list = view.findViewById(R.id.list);

        // connect list to adapter
        list.setAdapter(adapter);

        // set emptyView for list
        emptyView = view.findViewById(R.id.tvNoUpcomingEvents);
        list.setEmptyView(emptyView);

        // just findViewById doesn't work because fragment doesnt have that method, therefore need to use view
        // get all elements
        btnExhibition = view.findViewById(R.id.btnExhibition);
        btnFootball = view.findViewById(R.id.btnFootball);
        btnMarathon = view.findViewById(R.id.btnMarathon);
        btnFestival = view.findViewById(R.id.btnFestival);
        btnParty = view.findViewById(R.id.btnParty);
        btnStandUpTheater = view.findViewById(R.id.btnStandUpTheater);
        btnAll = view.findViewById(R.id.btnAll);
        btnAddEvent = view.findViewById(R.id.btnAddEvent);
        btnConcert = view.findViewById(R.id.btnConcert);

        // at the beginning all button is "clicked" so it is different color
        btnAll.setBackgroundColor(getResources().getColor(R.color.plum));

        list.setOnItemClickListener(this);
        btnExhibition.setOnClickListener(this);
        btnFootball.setOnClickListener(this);
        btnMarathon.setOnClickListener(this);
        btnFestival.setOnClickListener(this);
        btnParty.setOnClickListener(this);
        btnStandUpTheater.setOnClickListener(this);
        btnAll.setOnClickListener(this);
        btnAddEvent.setOnClickListener(this);
        btnConcert.setOnClickListener(this);
    }


    // parent - the AdapterView where the click happened
    // view - The view within the AdapterView that was clicked (this will be a view provided by the adapter)
    // position - position of the view in adapter
    // id - The row id of the item that was clicked.
    // id and position usually the same
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        // create intent
        Intent intent = new Intent(getContext(),
                EventDetailsActivity.class);

        // get event to transfer to next activity
        Event event = (Event) parent.getItemAtPosition(position);
        Bundle bundle1 = getArguments();
        String user = bundle1.getString("username");

        Bundle bundle = new Bundle();
        bundle.putString("nameOfEvent", event.getName());           // send name of event
        bundle.putString("username", user); // send username
        intent.putExtras(bundle);

        // start Activity
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.btnExhibition){
            // if a button is clicked reset the color of all buttons
            resetColors();
            btnExhibition.setBackgroundColor(getResources().getColor(R.color.plum));

            adapter.setEvents(dbHelper.getEventsByCategory("Exhibition"));

            currentCategory = "Exhibition";

        }else if(view.getId() == R.id.btnMarathon){
            // if a button is clicked reset the color of all buttons
            resetColors();

            btnMarathon.setBackgroundColor(getResources().getColor(R.color.plum));

            adapter.setEvents(dbHelper.getEventsByCategory("Marathon"));

            currentCategory = "Marathon";

        }else if(view.getId() == R.id.btnFootball){
            // if a button is clicked reset the color of all buttons
            resetColors();

            btnFootball.setBackgroundColor(getResources().getColor(R.color.plum));

            adapter.setEvents(dbHelper.getEventsByCategory("Football"));

            currentCategory = "Football";

        }else if(view.getId() == R.id.btnFestival){
            // if a button is clicked reset the color of all buttons
            resetColors();

            btnFestival.setBackgroundColor(getResources().getColor(R.color.plum));

            adapter.setEvents(dbHelper.getEventsByCategory("Festival"));

            currentCategory = "Festival";

        }else if(view.getId() == R.id.btnParty){
            // if a button is clicked reset the color of all buttons
            resetColors();

            btnParty.setBackgroundColor(getResources().getColor(R.color.plum));

            adapter.setEvents(dbHelper.getEventsByCategory("Party"));

            currentCategory = "Party";

        }else if(view.getId() == R.id.btnStandUpTheater){
            // if a button is clicked reset the color of all buttons
            resetColors();

            btnStandUpTheater.setBackgroundColor(getResources().getColor(R.color.plum));

            adapter.setEvents(dbHelper.getEventsByCategory("Stand-Up & Theater"));

            currentCategory = "Stand-Up & Theater";

        }else if(view.getId() == R.id.btnAll){
            // if a button is clicked reset the color of all buttons
            resetColors();

            btnAll.setBackgroundColor(getResources().getColor(R.color.plum));

            adapter.setEvents(dbHelper.getSortedEvents());

            currentCategory = "All";

        }else if(view.getId() == R.id.btnConcert){
            // if a button is clicked reset the color of all buttons
            resetColors();

            btnConcert.setBackgroundColor(getResources().getColor(R.color.plum));

            adapter.setEvents(dbHelper.getEventsByCategory("Concert"));

            currentCategory = "Concert";

        }else if(view.getId() == R.id.btnAddEvent){
            // go to CreateEventActivity

            Intent intent = new Intent(getActivity(), CreateEventActivity.class);
            startActivity(intent);
        }
    }

    public void resetColors(){
        btnExhibition.setBackgroundColor(getResources().getColor(R.color.purple_500));
        btnFootball.setBackgroundColor(getResources().getColor(R.color.purple_500));
        btnMarathon.setBackgroundColor(getResources().getColor(R.color.purple_500));
        btnFestival.setBackgroundColor(getResources().getColor(R.color.purple_500));
        btnParty.setBackgroundColor(getResources().getColor(R.color.purple_500));
        btnStandUpTheater.setBackgroundColor(getResources().getColor(R.color.purple_500));
        btnAll.setBackgroundColor(getResources().getColor(R.color.purple_500));
        btnAddEvent.setBackgroundColor(getResources().getColor(R.color.purple_500));
        btnConcert.setBackgroundColor(getResources().getColor(R.color.purple_500));
    }
    @Override
    public void onResume() {   // when back to fragment refresh the list - needed when coming back from CreateEventActivity
        super.onResume();
        // check to see if adapter exists
        if (adapter != null) {
            // get adapter to update/sort list by category
            if (currentCategory.equals("All")) {
                adapter.setEvents(dbHelper.getSortedEvents());
            } else {
                // get promoted events at the top
                adapter.setEvents(dbHelper.getEventsByCategory(currentCategory));
            }
        }

        // notify list that it has been modified
        adapter.notifyDataSetChanged();
    }
}