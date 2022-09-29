package com.joek.nuxtube.data.model.history;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {History.class}, version = 1, exportSchema = false)

public abstract class HistoryDatabase extends RoomDatabase {
    abstract HistoryDao historyDao();

    private static volatile HistoryDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static HistoryDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (HistoryDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    HistoryDatabase.class, "history_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
