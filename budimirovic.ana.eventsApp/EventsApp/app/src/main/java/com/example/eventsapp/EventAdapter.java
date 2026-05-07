package com.example.eventsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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
    // convertView is
    // ViewGroup is
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null){   // if there is no view
            // make view - inflate the xml where the element of the list is described
            convertView = inflater.inflate(R.layout.event_item, parent, false);

            // create new Holder
            holder = new ViewHolder();
            // find elements to put in viewHolder
            holder.image = convertView.findViewById(R.id.eventImage);
            holder.name = convertView.findViewById(R.id.tvName);
            holder.category = convertView.findViewById(R.id.tvCategory);
            holder.location = convertView.findViewById(R.id.tvLocation);
            holder.dateAndTime = convertView.findViewById(R.id.tvdateAndTime);
            holder.featured = convertView.findViewById(R.id.tvPromoted);
            holder.freeSeats = convertView.findViewById(R.id.tvFreeSeats);

            // set Tag
            convertView.setTag(holder);
        }else{
            // if the view already exists
            // when called the first time all the ViewHolder elements were put in Tag
            // when called any other time just use .getTag() to get all the elements from it
            // elements are addresses of ViewHolder elements

            //.getTag() returns Object of any type, so we need to cast it to ViewHolder
            holder = (ViewHolder) convertView.getTag();
        }

        Event event =events.get(position);

        holder.image.setImageResource(event.getImageResId());
        holder.name.setText(event.getName());
        holder.category.setText(event.getCategory());
        holder.location.setText(event.getLocation());
        holder.dateAndTime.setText(event.getDateTime());
        // if an event is promoted set visibility of tvFeatured to VISIBLE
        // and make freeSeats visible
        if(event.isPromoted()){
            holder.featured.setVisibility(View.VISIBLE);
            holder.freeSeats.setVisibility(View.VISIBLE);
            holder.freeSeats.setText(String.valueOf(event.getCapacity()));
        }else{
            holder.featured.setVisibility(View.GONE);
            holder.freeSeats.setVisibility(View.GONE);
        }

        return convertView;
    }

    // ViewHolder sablon, used to store elements so that findViewById() isn't repeatedly called
    static class ViewHolder {
        ImageView image;
        TextView name;
        TextView category;
        TextView location;
        TextView dateAndTime;
        TextView featured;
        TextView freeSeats;
    }

    public void setEvents(ArrayList<Event> events){
        this.events.clear();           // delete old elements
        this.events.addAll(events);    // add new
        notifyDataSetChanged();

    }

    // delete all elements of the list
    public void deleteEvents(){
        this.events.clear();
        notifyDataSetChanged();
    }

}
