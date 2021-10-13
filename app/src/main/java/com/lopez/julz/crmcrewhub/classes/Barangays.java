package com.lopez.julz.crmcrewhub.classes;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Barangays {
    @PrimaryKey
    @NonNull
    private String id;

    @ColumnInfo(name = "Barangay")
    private String Barangay;

    @ColumnInfo(name = "TownId")
    private String TownId;

    @ColumnInfo(name = "Notes")
    private String Notes;

    public Barangays(@NonNull String id, String barangay, String townId, String notes) {
        this.id = id;
        Barangay = barangay;
        TownId = townId;
        Notes = notes;
    }

    public Barangays() {}

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getBarangay() {
        return Barangay;
    }

    public void setBarangay(String barangay) {
        Barangay = barangay;
    }

    public String getTownId() {
        return TownId;
    }

    public void setTownId(String townId) {
        TownId = townId;
    }

    public String getNotes() {
        return Notes;
    }

    public void setNotes(String notes) {
        Notes = notes;
    }
}
