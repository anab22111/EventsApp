package com.example.eventsapp;

import android.os.IBinder;
import android.os.RemoteException;

public class counterBinderClass extends counterBinder.Stub {
    private int counter = 0;

    @Override
    public void setValue(int val) throws RemoteException {
        this.counter = val;

    }
    @Override
    public int getValue() throws RemoteException {
        return this.counter;
    }
}
