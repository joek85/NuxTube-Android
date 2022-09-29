package com.joek.nuxtube.data.model.history;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;

public class HistoryRepository {
    private final HistoryDao historyDao;
    private final LiveData<List<History>> mHistory;

    // Note that in order to unit test the WordRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public HistoryRepository(Application application) {
        HistoryDatabase db = HistoryDatabase.getDatabase(application);
        historyDao = db.historyDao();
        mHistory = historyDao.getAllHistory();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    LiveData<List<History>> getAllHistory() {
        return mHistory;
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    void insert(History history) {
        HistoryDatabase.databaseWriteExecutor.execute(() -> {
            historyDao.insert(history);
        });
    }
    void clearHistory(){
        HistoryDatabase.databaseWriteExecutor.execute(historyDao::deleteAll);
    }
    void deleteHistory(int id) {
        HistoryDatabase.databaseWriteExecutor.execute(() -> historyDao.deleteHistory(id));
    }
}
