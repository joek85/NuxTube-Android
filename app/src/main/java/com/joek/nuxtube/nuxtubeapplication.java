package com.joek.nuxtube;

import android.app.Application;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class nuxtubeapplication extends Application {
    ExecutorService executorService = Executors.newFixedThreadPool(4);
}
