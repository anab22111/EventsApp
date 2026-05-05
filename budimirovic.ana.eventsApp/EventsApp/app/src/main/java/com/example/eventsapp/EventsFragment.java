package com.example.eventsapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

///**
// * A simple {@link Fragment} subclass.
// * Use the {@link EventsFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class EventsFragment extends Fragment {
    public EventsFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_events, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        // just findViewById doesnt work because the fragment doesnt have that method, therefore need to use view
        ListView list = view.findViewById(R.id.list);
        TextView emptyView = view.findViewById(R.id.tvNoUpcomingEvents);

        // set empty view of list to tvNoUpcomingEvents
        list.setEmptyView(emptyView);
    }


}