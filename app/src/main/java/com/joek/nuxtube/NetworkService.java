package com.joek.nuxtube;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class NetworkService extends Service {
    public NetworkService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public static void doSearch(String searchQuery){

    }
}