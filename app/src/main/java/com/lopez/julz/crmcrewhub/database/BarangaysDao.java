package com.lopez.julz.crmcrewhub.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.lopez.julz.crmcrewhub.classes.Barangays;
import com.lopez.julz.crmcrewhub.classes.Towns;

import java.util.List;

@Dao
public interface BarangaysDao {
    @Query("SELECT * FROM Barangays")
    List<Barangays> getAll();

    @Query("SELECT * FROM Barangays WHERE TownId = :id")
    List<Barangays> getAllByTownId(String id);

    @Insert
    void insertAll(Barangays... barangays);

    @Update
    void updateAll(Barangays... barangays);

    @Query("SELECT * FROM Barangays WHERE id = :id")
    Barangays getOne(String id);
}
