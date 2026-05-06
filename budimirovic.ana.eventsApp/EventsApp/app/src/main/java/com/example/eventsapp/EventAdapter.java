package com.example.eventsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class EventAdapter extends BaseAdapter {
    private final Context context;
    private final ArrayList<Event> events;
    private final LayoutInflater inflater;

    public EventAdapter(Context context, ArrayList<Event> events) {
        this.context = context;
        this.events = events;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Object getItem(int position) {
        return events.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // getView() returns View - one element of the list
    // position is the position in the list
    // converView is
    // ViewGroup is
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RecyclerView.ViewHolder holder;

        if(convertView == null){
            convertView = inflater.inflate(R.layout.event_item, parent, false);

        }

    }
}
