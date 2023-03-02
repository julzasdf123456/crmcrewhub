package com.lopez.julz.crmcrewhub.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.lopez.julz.crmcrewhub.classes.Barangays;
import com.lopez.julz.crmcrewhub.classes.Towns;

@Database(entities = {Users.class,
        Barangays.class,
        Towns.class,
        ServiceConnections.class,
        ServiceConnectionInspections.class,
        TimeFrames.class,
        TicketRepositories.class,
        Tickets.class,
        Crew.class,
        AppConfig.class,
        Settings.class,
        StationCrews.class}, version = 59)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UsersDao usersDao();

    public abstract TownsDao townsDao();

    public abstract BarangaysDao barangaysDao();

    public abstract ServiceConnectionsDao serviceConnectionsDao();

    public abstract ServiceConnectionInspectionsDao serviceConnectionInspectionsDao();

    public abstract TimeFramesDao timeFramesDao();

    public abstract TicketRepositoriesDao ticketRepositoriesDao();

    public abstract TicketsDao ticketsDao();

    public abstract CrewDao crewDao();

    public abstract AppConfigDao appConfigDao();

    public abstract SettingsDao settingsDao();

    public abstract StationCrewsDao stationCrewsDao();
}
