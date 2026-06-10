package com.example.eventsapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.

 */
public class FriendsFragment extends Fragment {

    private String username;
    private TextView tvFriends;
    private ListView listView;
    private ArrayList<String> friendsActivities;
    private dbHelper helper;

    public FriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // get username from arguments
        if (getArguments() != null) {
            username = getArguments().getString("username");
        }

        listView = view.findViewById(R.id.listFriendsEvents);
        tvFriends = view.findViewById(R.id.tvFriends);
        helper = new dbHelper(getContext());
        friendsActivities = new ArrayList<>();

        getFriendsActivitiesFromServer();
    }

    private void getFriendsActivitiesFromServer(){
        // get userId from server
        String userId = getServerUserId(username);

        // make thread to send request to server
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                HttpHelper httpHelper = new HttpHelper();
                String url = "/friends-activity/" + userId;

                try {
                    // send request
                    JSONArray jsonArray = httpHelper.getJSONArrayFromUrl(url);

                    // if server returned JSONArray
                    if (jsonArray != null) {
                        // go through all objects, every object has .username .eventName and .commitment
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject obj = jsonArray.getJSONObject(i);
                            String msg = obj.getString("username") + " is also " +
                                    (obj.getString("commitment").equals("PRISUSTVUJE") ? "attending " : "interested in ") +
                                    obj.getString("eventName");
                            friendsActivities.add(msg);   // add to list
                        }
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

                if(getActivity() != null){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // make adapter and connect to list
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                                    android.R.layout.simple_list_item_1, friendsActivities);
                            listView.setAdapter(adapter);
                        }
                    });
                }
            }
        });
        thread.start();
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