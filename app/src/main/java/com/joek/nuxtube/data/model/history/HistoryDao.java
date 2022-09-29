package com.joek.nuxtube.data.model.history;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;
@Dao
public interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(History history);

    @Query("DELETE FROM history_table")
    void deleteAll();

    @Query("SELECT * FROM history_table ORDER BY id DESC")
    LiveData<List<History>> getAllHistory();

    @Query("DELETE FROM history_table WHERE id = :historyId")
    void deleteHistory(int historyId);
}
