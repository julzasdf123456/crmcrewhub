package com.lopez.julz.crmcrewhub.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TicketsDao {
    @Query("SELECT * FROM Tickets WHERE Trash IS NULL AND (UploadStatus IS NULL OR UploadStatus = 'UPLOADABLE') ORDER BY ConsumerName")
    List<Tickets> getAll();

    @Insert
    void insertAll(Tickets... tickets);

    @Update
    void updateAll(Tickets... tickets);

    @Query("SELECT * FROM Tickets WHERE id = :id")
    Tickets getOne(String id);

    @Query("SELECT * FROM Tickets WHERE id = :id AND (UploadStatus IS NULL OR UploadStatus = 'UPLOADABLE')")
    Tickets getOneValidation(String id);

    @Query("SELECT * FROM Tickets WHERE UploadStatus = 'UPLOADABLE'")
    List<Tickets> getUploadableTickets();

    @Query("DELETE FROM Tickets WHERE id = :id")
    void deleteOne(String id);

    @Query("SELECT * FROM Tickets ORDER BY ConsumerName")
    List<Tickets> getArchive();

    @Query("SELECT * FROM Tickets WHERE ConsumerName LIKE :regex OR id LIKE :regex")
    List<Tickets> getArchiveSearch(String regex);
}
