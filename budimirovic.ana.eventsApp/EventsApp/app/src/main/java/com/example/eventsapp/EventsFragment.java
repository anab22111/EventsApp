package com.example.eventsapp;

import android.content.Intent;
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

    public EventsFragment(){}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_events, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        // get list from AppData, get sorted events so that promoted events are always at the beginning
        events = new ArrayList<>(AppData.getSortedEvents());

        // make adapter for list
        adapter = new EventAdapter(getContext(), events);    // have to use getContext bc fragment isn't an Activity

        list = view.findViewById(R.id.list);

        // connect list to adapter
        list.setAdapter(adapter);

        // set emptyView for list
        emptyView = view.findViewById(R.id.tvNoUpcomingEvents);
        list.setEmptyView(emptyView);

        // just findViewById doesnt work because fragment doesnt have that method, therefore need to use view
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

        // send only name
        intent.putExtra("nameOfEvent", event.getName());

        // start Activity
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {

        // if a button is clicked reset the color of all buttons
        resetColors();

        if(view.getId() == R.id.btnExhibition){
            btnExhibition.setBackgroundColor(getResources().getColor(R.color.plum));

            adapter.setEvents(AppData.getEventsByCategory("Exhibition"));

        }else if(view.getId() == R.id.btnMarathon){

            btnMarathon.setBackgroundColor(getResources().getColor(R.color.plum));

            adapter.setEvents(AppData.getEventsByCategory("Marathon"));

        }else if(view.getId() == R.id.btnFootball){

            btnFootball.setBackgroundColor(getResources().getColor(R.color.plum));

            adapter.setEvents(AppData.getEventsByCategory("Football"));

        }else if(view.getId() == R.id.btnFestival){

            btnFestival.setBackgroundColor(getResources().getColor(R.color.plum));

            adapter.setEvents(AppData.getEventsByCategory("Festival"));

        }else if(view.getId() == R.id.btnParty){

            btnParty.setBackgroundColor(getResources().getColor(R.color.plum));

            adapter.setEvents(AppData.getEventsByCategory("Party"));

        }else if(view.getId() == R.id.btnStandUpTheater){

            btnStandUpTheater.setBackgroundColor(getResources().getColor(R.color.plum));

            adapter.setEvents(AppData.getEventsByCategory("Stand-Up & Theater"));

        }else if(view.getId() == R.id.btnAll){

            btnAll.setBackgroundColor(getResources().getColor(R.color.plum));

            adapter.setEvents(AppData.getSortedEvents());

        }else if(view.getId() == R.id.btnConcert){

            btnConcert.setBackgroundColor(getResources().getColor(R.color.plum));

            adapter.setEvents(AppData.getEventsByCategory("Concert"));

        }else if(view.getId() == R.id.btnAddEvent){
            // go to CreateEventActivity

            Intent intent = new Intent(getActivity(), CreateEventActivity.class);
            startActivity(intent);
        }
    }

    public void resetColors(){
        btnExhibition.setBackgroundColor(getResources().getColor(R.color.purple_200));
        btnFootball.setBackgroundColor(getResources().getColor(R.color.purple_200));
        btnMarathon.setBackgroundColor(getResources().getColor(R.color.purple_200));
        btnFestival.setBackgroundColor(getResources().getColor(R.color.purple_200));
        btnParty.setBackgroundColor(getResources().getColor(R.color.purple_200));
        btnStandUpTheater.setBackgroundColor(getResources().getColor(R.color.purple_200));
        btnAll.setBackgroundColor(getResources().getColor(R.color.purple_200));
        btnAddEvent.setBackgroundColor(getResources().getColor(R.color.purple_200));
        btnConcert.setBackgroundColor(getResources().getColor(R.color.purple_200));
    }
    @Override
    public void onResume() {   // when back to fragment refresh the list - needed when coming back from CreateEventActivity
        super.onResume();
        // check to see if adapter exists
        if (adapter != null) {
            // update data set/list
            adapter.notifyDataSetChanged();
        }




    }
}