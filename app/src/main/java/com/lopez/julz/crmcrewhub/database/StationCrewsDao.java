package com.lopez.julz.crmcrewhub.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;


@Dao
public interface StationCrewsDao {
    @Query("SELECT * FROM StationCrews")
    List<StationCrews> getAll();

    @Insert
    void insertAll(StationCrews... stationCrews);

    @Update
    void updateAll(StationCrews... updateAll);

    @Query("SELECT * FROM StationCrews WHERE id=:id")
    StationCrews getOne(String id);
}
