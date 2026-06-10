package com.example.eventsapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Service24h extends Service {

    private final String channelId = "specialEvents.notifications";   // unique channel ID for notifications

    private final String description = "Notifications for special events";    // description for the notification channel

    private final int notificationId = 1234;   // unique identifier for the notification
    private final int notificationClosedId = 5648;
    // initialize binder
    private  counterBinderClass mBinder = null;
    private Thread mainThread = null;

    private int specEventVersion = 1;
    public Service24h() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        if(mBinder == null){
            // make binder
            mBinder = new counterBinderClass();
        }
        if (mainThread != null && !mainThread.isAlive()) {
            mainThread.start();
        }
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // create a notification channel
        createNotificationChannel();

        // load version of spec event from sharedpreference
        SharedPreferences sp = getSharedPreferences("EventsAppPrefs", MODE_PRIVATE);
        specEventVersion = sp.getInt("spec-event-version", 1);


        // when service is created start thread that counts
        mainThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // set time for one cycle
                long durationOfOneCycle = 60 * 1000;    // 60 in testing = 24 h in real time
                while(true){
                    long startTime = System.currentTimeMillis();    // get start time of cycle

                    // special event is created every 24h - 60sec
                    // first special event is created when user logs in/registers
                    String newEventId = createSpecialEvent();

                    // time window for registration needs to be put in sharedPreferences
                    long timeWindow = 15 * 1000;
                    long expirationTime = System.currentTimeMillis() + timeWindow;

                    SharedPreferences sp = getSharedPreferences("EventsAppPrefs", MODE_PRIVATE);
                    sp.edit().putLong("exp-time", expirationTime).apply();

                    ////// SEND NOTIFICATION
                    // get data from SharedPreferences to send to next activity
                    String savedUsername = sp.getString("username", "");
                    String savedEventName = sp.getString("last-special-event-name", "Special event");

                    // intent that triggers when the notification is tapped
                    Intent intent = new Intent(Service24h.this, EventDetailsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("nameOfEvent", savedEventName);
                    intent.putExtra("username", savedUsername);
                    PendingIntent pendingIntent = PendingIntent.getActivity(
                            Service24h.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                    );

                    String textTitle = "New SPECIAL Event!";
                    String textContent = "Don't miss out! You only have 5 minutes to register!";
                    NotificationCompat.Builder builder =
                            new NotificationCompat.Builder(Service24h.this, channelId)
                                    .setSmallIcon(R.drawable.party)
                                    .setContentTitle(textTitle)
                                    .setContentText(textContent)
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                    .setContentIntent(pendingIntent)
                                    .setOnlyAlertOnce(true)
                                    .setAutoCancel(true);

                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(Service24h.this);
                    // start inner counter for 5 minutes - 15 seconds
                    int secondsLeft = (int)(timeWindow/1000);

                    // inner counter counting how many sec are left for user to register
                    while(secondsLeft >= 0){
                        if(mBinder != null){
                            try {
                                mBinder.setValue(secondsLeft);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }

                        // update notification text
                        int minutes = secondsLeft / 60;
                        int seconds = secondsLeft % 60;

                        builder.setContentText(String.format("Don't miss out! You have %02d:%02d left to register!", minutes, seconds));

                        try {
                            notificationManager.notify(notificationId, builder.build());
                        } catch (SecurityException e) {
                            e.printStackTrace();
                        }

                        // pause for one second
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        secondsLeft--;
                    }

                    // when timeWindow for registration expires cancel previous notification
                    notificationManager.cancel(notificationId);
                    sendWindowCloseNotification(notificationManager);

                    // wait for 60s/24h
                    long elapsed = System.currentTimeMillis() - startTime;
                    long remaining = durationOfOneCycle - elapsed;

                    if(remaining > 0){
                        try {
                            Thread.sleep(remaining);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        });
        mainThread.start();
    }

    private void sendWindowCloseNotification(NotificationManagerCompat notificationManager){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.party)
                .setContentTitle("Time for registration expired!")
                .setContentText("Next chance to register will be in 24h!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        try {
            notificationManager.notify(notificationClosedId, builder.build());
        } catch (SecurityException e) {
            e.printStackTrace();
        }

       // increase version for next time
        specEventVersion++;
        SharedPreferences sp = getSharedPreferences("EventsAppPrefs", MODE_PRIVATE);
        sp.edit().putInt("spec-event-version", specEventVersion).apply();
    }

    private String createSpecialEvent(){

        // send POST request to server
        // create json for server
        String name = "SURPRISE Special event v" + specEventVersion ;
        String description = "Come to a surprise special event!";
        String location = "Spens, Novi Sad";
        String dateTime = "22.11.2026. 18:00";
        String category = "Special";
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
        String url = "/events";
        HttpHelper httpHelper = new HttpHelper();

        try {

            JSONObject serverResponse = null;
            String errorText = null;

            try {
                serverResponse = httpHelper.postJSONObjectFromURL(url, data, "POST");
            } catch (IOException e) {
                android.util.Log.e("Service24h", "IOException: " + e.getMessage());

                errorText = "Server unreachable.";
            } catch (JSONException e) {
                android.util.Log.e("Service24h", "jsonex: " + e.getMessage());

                errorText = "Server didn't return JSON.";
            }catch (Exception e) {
                android.util.Log.e("Service24h", "exc: " + e.getMessage());

                errorText = "Error " + e.getMessage();
            }

            final JSONObject response = serverResponse;
            final String finalErrorText = errorText;

            if (finalErrorText != null) {
                android.util.Log.e("Service24h", "ERROR: " + finalErrorText);
                return "";
            }


            if (response != null) {
                int statusCode = response.optInt("http_status_code", 0);
                if (statusCode == 200) {
                    android.util.Log.d("Service24h", "Success");

                    String serverId = response.optString("_id");
                    // add to local base
                    addToLocalDB(name, description, location, dateTime, category, isPromoted, capacity, serverId);

                    // add event name to sharedPreferences
                    SharedPreferences sp = getSharedPreferences("EventsAppPrefs", MODE_PRIVATE);
                    sp.edit().putString("current-special-event-id", serverId).apply();

                    return serverId;
                }
            }
        } catch (Exception e) {
            android.util.Log.e("Service24h", "Error: " + e.getMessage());
        }

        return "";
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

    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    channelId,
                    description,
                    NotificationManager.IMPORTANCE_HIGH
            );

            notificationChannel.enableLights(true); // turn on notification light
            notificationChannel.enableVibration(true); // allow vibration for notifications

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
    }
}