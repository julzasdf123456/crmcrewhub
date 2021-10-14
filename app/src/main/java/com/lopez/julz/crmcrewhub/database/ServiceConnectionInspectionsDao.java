package com.lopez.julz.crmcrewhub.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ServiceConnectionInspectionsDao {
    @Query("SELECT * FROM ServiceConnectionInspections")
    List<ServiceConnectionInspections> getAll();

    @Insert
    void insertAll(ServiceConnectionInspections... serviceConnectionInspections);

    @Update
    void updateAll(ServiceConnectionInspections... serviceConnectionInspections);

    @Query("SELECT * FROM ServiceConnectionInspections WHERE id = :id")
    ServiceConnectionInspections getOneById(String id);

    @Query("SELECT * FROM ServiceConnectionInspections WHERE ServiceConnectionId = :scId")
    ServiceConnectionInspections getOneByServiceConnectionId(String scId);

    @Query("DELETE FROM ServiceConnectionInspections")
    void deleteAll();

    @Query("DELETE FROM ServiceConnectionInspections WHERE ServiceConnectionId = :scId")
    void deleteByServiceConnectionId(String scId);
}
