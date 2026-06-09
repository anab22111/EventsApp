package com.example.eventsapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

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
    }
}