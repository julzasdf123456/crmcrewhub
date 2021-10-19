package com.lopez.julz.crmcrewhub.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class TimeFrames {
    @PrimaryKey
    @NonNull
    private String id;

    @ColumnInfo(name = "ServiceConnectionId")
    private String ServiceConnectionId;

    @ColumnInfo(name = "User")
    private String User;

    @ColumnInfo(name = "ArrivalDate")
    private String ArrivalDate;

    @ColumnInfo(name = "EnergizationDate")
    private String EnergizationDate;

    @ColumnInfo(name = "Status")
    private String Status;

    @ColumnInfo(name = "Reason")
    private String Reason;

    @ColumnInfo(name = "Notes")
    private String Notes;

    @ColumnInfo(name = "IsUploaded")
    private String IsUploaded;

    @ColumnInfo(name = "created_at")
    private String created_at;

    @ColumnInfo(name = "updated_at")
    private String updated_at;

    public TimeFrames() {}

    public TimeFrames(@NonNull String id, String serviceConnectionId, String user, String arrivalDate, String energizationDate, String status, String reason, String notes, String isUploaded, String created_at, String updated_at) {
        this.id = id;
        ServiceConnectionId = serviceConnectionId;
        User = user;
        ArrivalDate = arrivalDate;
        EnergizationDate = energizationDate;
        Status = status;
        Reason = reason;
        Notes = notes;
        IsUploaded = isUploaded;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getServiceConnectionId() {
        return ServiceConnectionId;
    }

    public void setServiceConnectionId(String serviceConnectionId) {
        ServiceConnectionId = serviceConnectionId;
    }

    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        User = user;
    }

    public String getArrivalDate() {
        return ArrivalDate;
    }

    public void setArrivalDate(String arrivalDate) {
        ArrivalDate = arrivalDate;
    }

    public String getEnergizationDate() {
        return EnergizationDate;
    }

    public void setEnergizationDate(String energizationDate) {
        EnergizationDate = energizationDate;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getReason() {
        return Reason;
    }

    public void setReason(String reason) {
        Reason = reason;
    }

    public String getNotes() {
        return Notes;
    }

    public void setNotes(String notes) {
        Notes = notes;
    }

    public String getIsUploaded() {
        return IsUploaded;
    }

    public void setIsUploaded(String isUploaded) {
        IsUploaded = isUploaded;
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
}

