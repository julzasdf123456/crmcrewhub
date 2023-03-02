package com.lopez.julz.crmcrewhub.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class AppConfig {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int id;

    @ColumnInfo(name = "DeviceStation")
    private String DeviceStation;

    @ColumnInfo(name = "CrewLeader")
    private String CrewLeader;

    public AppConfig() {
    }

    public AppConfig(int id, String deviceStation) {
        this.id = id;
        DeviceStation = deviceStation;
    }

    public AppConfig(String deviceStation) {
        DeviceStation = deviceStation;
    }

    public AppConfig(String deviceStation, String crewLeader) {
        DeviceStation = deviceStation;
        CrewLeader = crewLeader;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDeviceStation() {
        return DeviceStation;
    }

    public void setDeviceStation(String deviceStation) {
        DeviceStation = deviceStation;
    }

    public String getCrewLeader() {
        return CrewLeader;
    }

    public void setCrewLeader(String crewLeader) {
        CrewLeader = crewLeader;
    }
}
