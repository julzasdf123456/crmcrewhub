package com.lopez.julz.crmcrewhub.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CrewDao {
    @Query("SELECT * FROM Crew ORDER BY StationName")
    List<Crew> getAll();

    @Insert
    void insertAll(Crew... crew);

    @Update
    void updateAll(Crew... crew);

    @Query("SELECT * FROM Crew WHERE id = :id")
    Crew getOne(String id);

}
