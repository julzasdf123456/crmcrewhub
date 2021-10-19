package com.lopez.julz.crmcrewhub.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ServiceConnectionsDao {
    @Query("SELECT * FROM ServiceConnections")
    List<ServiceConnections> getAll();

    @Insert
    void insertAll(ServiceConnections... serviceConnections);

    @Update
    void updateAll(ServiceConnections... serviceConnections);

    @Query("SELECT * FROM ServiceConnections WHERE id = :id")
    ServiceConnections getOne(String id);

    @Query("SELECT * FROM ServiceConnections WHERE Status = 'Approved' OR Status = 'Not Energized' OR Status = 'Energized'")
    List<ServiceConnections> getQueue();

    @Query("DELETE FROM ServiceConnections")
    void deleteAll();

    @Query("DELETE FROM ServiceConnections WHERE id = :id")
    void deleteOne(String id);

    @Query("SELECT * FROM ServiceConnections WHERE Status='Energized' AND (DateTimeOfEnergization IS NOT NULL OR DateTimeOfEnergization != '') AND (DateTimeLinemenArrived IS NOT NULL OR DateTimeLinemenArrived != '')")
    List<ServiceConnections> getEnergized();
}
