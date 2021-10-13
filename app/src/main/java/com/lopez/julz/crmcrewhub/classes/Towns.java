package com.lopez.julz.crmcrewhub.classes;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Towns {
    @PrimaryKey
    @NonNull
    private String id;

    @ColumnInfo(name = "Town")
    private String Town;

    @ColumnInfo(name = "District")
    private String District;

    @ColumnInfo(name = "Station")
    private String Station;

    public Towns(){}

    public Towns(@NonNull String id, String town, String district, String station) {
        this.id = id;
        Town = town;
        District = district;
        Station = station;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getTown() {
        return Town;
    }

    public void setTown(String town) {
        Town = town;
    }

    public String getDistrict() {
        return District;
    }

    public void setDistrict(String district) {
        District = district;
    }

    public String getStation() {
        return Station;
    }

    public void setStation(String station) {
        Station = station;
    }
}
