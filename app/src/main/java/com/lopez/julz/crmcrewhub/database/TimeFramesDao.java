package com.lopez.julz.crmcrewhub.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TimeFramesDao {
    @Query("SELECT * FROM TimeFrames WHERE IsUploaded = 'No'")
    List<TimeFrames> getUnuploaded();

    @Insert
    void insertAll(TimeFrames... timeFrames);

    @Update
    void updateAll(TimeFrames... timeFrames);
}
