package com.lopez.julz.crmcrewhub.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface AppConfigDao {
    @Query("SELECT * FROM AppConfig WHERE id = 1")
    AppConfig getConfig();

    @Insert
    void insertAll(AppConfig... appConfigs);

    @Update
    void updateAll(AppConfig... appConfigs);
}
