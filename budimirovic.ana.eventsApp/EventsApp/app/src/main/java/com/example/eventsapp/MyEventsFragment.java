package com.example.eventsapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

///**
// * A simple {@link Fragment} subclass.
// * Use the {@link MyEventsFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class MyEventsFragment extends Fragment implements View.OnClickListener{

    public MyEventsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_my_events, container, false);

        Button btnInterestedEvents = view.findViewById(R.id.btnInterestedEvents);
        Button btnAttendingEvents = view.findViewById(R.id.btnAttendingEvents);
        Button btnMyProfile = view.findViewById(R.id.btnMyProfile);

        btnAttendingEvents.setOnClickListener(this);
        btnInterestedEvents.setOnClickListener(this);
        btnMyProfile.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.btnInterestedEvents){
            Intent intent = new Intent(getActivity(), InterestedEventsActivity.class);
            startActivity(intent);
        }else if(view.getId() == R.id.btnAttendingEvents){
            Intent intent = new Intent(getActivity(), AttendingEventsActivity.class);
            startActivity(intent);
        }else if(view.getId() == R.id.btnMyProfile){
            Intent intent = new Intent(getActivity(), ProfileActivity.class);
            startActivity(intent);
        }
    }
}