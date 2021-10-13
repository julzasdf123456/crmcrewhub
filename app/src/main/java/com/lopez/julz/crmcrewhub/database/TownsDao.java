package com.lopez.julz.crmcrewhub.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.lopez.julz.crmcrewhub.classes.Towns;

import java.util.List;

@Dao
public interface TownsDao {
    @Query("SELECT * FROM Towns ORDER BY Town")
    List<Towns> getAll();

    @Insert
    void insertAll(Towns... towns);

    @Update
    void updateAll(Towns... towns);

    @Query("SELECT * FROM Towns WHERE id = :id")
    Towns getOne(String id);
}
