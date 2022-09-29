package com.joek.nuxtube.data.model.history;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class HistoryViewModel extends AndroidViewModel {

    private HistoryRepository historyRepository;
    private final LiveData<List<History>> history;


    public HistoryViewModel(@NonNull Application application) {
        super(application);
        historyRepository = new HistoryRepository(application);
        history = historyRepository.getAllHistory();
    }

    public LiveData<List<History>> getHistory() {
        return history;
    }
    public void insert(History history) { historyRepository.insert(history); }
    public void clearHistory(){
        historyRepository.clearHistory();
    }
    public void deleteHistory(int id) {
        historyRepository.deleteHistory(id);
    }

}
