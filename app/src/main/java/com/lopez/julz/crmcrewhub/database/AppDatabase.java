package com.lopez.julz.crmcrewhub.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.lopez.julz.crmcrewhub.classes.Barangays;
import com.lopez.julz.crmcrewhub.classes.Towns;

@Database(entities = {Users.class, Barangays.class, Towns.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UsersDao usersDao();

    public abstract TownsDao townsDao();

    public abstract BarangaysDao barangaysDao();

}
