package com.example.eventsapp;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Service24h extends Service {
    // initialize thread that will count seconds
    public Service24h() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // when service is created start thread that counts
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // set time for one cycle
                long durationOfOneCycle = 60;    // 60 in testing = 24 h in real time
                while(true){
                    long startTime = System.currentTimeMillis();    // get start time of cycle

                    // special event is created every 24h - 60sec
                    // first special event is created when user logs in/registers
                    createSpecialEvent();

                    // send notification to user that an event was created and he has 5 minutes to register
                    sendPushNotification();





                }




            }
        });
        thread.start();
    }

    private void createSpecialEvent(){

        // send POST request to server
        // create json for server
        String name = "Special event";
        String description = "";
        String location = "";
        String dateTime = "";
        String category = "";
        boolean isPromoted = true;
        int capacity = 100;

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
            e.printStackTrace();
        }

        // send to server, no Thread needed bc this is already in thread for server
        String url = "http://10.194.239.97:3000/events";
        HttpHelper httpHelper = new HttpHelper();

        try {
            JSONObject response = httpHelper.postJSONObjectFromURL(url, data, "POST");

            if (response != null) {
                int statusCode = response.optInt("http_status_code", 0);
                if (statusCode == 200) {
                    android.util.Log.d("Service24h", "Success");

                    String serverId = response.optString("_id");
                    // add to local base
                    addToLocalDB(name, description, location, dateTime, category, isPromoted, capacity, serverId);
                }
            }
        } catch (Exception e) {
            android.util.Log.e("Service24h", "Error: " + e.getMessage());
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
        }

        return imageRes;
    }

    private void sendPushNotification(){

        // set notification content
        String textTitle = "New SPECIAL Event!";
        String textContent = "Don't miss out! You only have 5 minutes to register!";
        NotificationCompat.Builder builder =
//                new NotificationCompat.Builder(this, CHANNEL_ID)
//                        .setSmallIcon(R.drawable.ic_logo)
//                        .setContentTitle(textTitle)
//                        .setContentText(textContent)
//                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
    }
}