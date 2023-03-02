package com.lopez.julz.crmcrewhub.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class StationCrews {
    @PrimaryKey
    @NonNull
    private String id;

    @ColumnInfo(name = "StationName")
    private String StationName;

    @ColumnInfo(name = "CrewLeader")
    private String CrewLeader;

    @ColumnInfo(name = "Members")
    private String Members;

    @ColumnInfo(name = "Notes")
    private String Notes;

    @ColumnInfo(name = "created_at")
    private String created_at;

    @ColumnInfo(name = "updated_at")
    private String updated_at;

    @ColumnInfo(name = "IsActive")
    private String IsActive;

    public StationCrews(@NonNull String id, String stationName, String crewLeader, String members, String notes, String created_at, String updated_at, String isActive) {
        this.id = id;
        StationName = stationName;
        CrewLeader = crewLeader;
        Members = members;
        Notes = notes;
        this.created_at = created_at;
        this.updated_at = updated_at;
        IsActive = isActive;
    }

    public StationCrews() {
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getStationName() {
        return StationName;
    }

    public void setStationName(String stationName) {
        StationName = stationName;
    }

    public String getCrewLeader() {
        return CrewLeader;
    }

    public void setCrewLeader(String crewLeader) {
        CrewLeader = crewLeader;
    }

    public String getMembers() {
        return Members;
    }

    public void setMembers(String members) {
        Members = members;
    }

    public String getNotes() {
        return Notes;
    }

    public void setNotes(String notes) {
        Notes = notes;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getIsActive() {
        return IsActive;
    }

    public void setIsActive(String isActive) {
        IsActive = isActive;
    }
}
