package com.lopez.julz.crmcrewhub.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TicketRepositoriesDao {
    @Query("SELECT * FROM TicketRepositories WHERE ParentTicket IS NOT NULL ORDER BY ParentTicket")
    List<TicketRepositories> getTicketTypes();

    @Insert
    void insertAll(TicketRepositories... ticketRepositories);

    @Update
    void updateAll(TicketRepositories... ticketRepositories);

    @Query("SELECT * FROM TicketRepositories WHERE id = :id")
    TicketRepositories getOne(String id);
}
