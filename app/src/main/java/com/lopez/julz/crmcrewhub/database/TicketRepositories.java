package com.lopez.julz.crmcrewhub.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class TicketRepositories {
    @PrimaryKey
    @NonNull
    private String id;

    @ColumnInfo(name = "Name")
    private String Name;

    @ColumnInfo(name = "Description")
    private String Description;

    @ColumnInfo(name = "ParentTicket")
    private String ParentTicket;

    @ColumnInfo(name = "Type")
    private String Type;

    @ColumnInfo(name = "KPSCategory")
    private String KPSCategory;

    @ColumnInfo(name = "KPSIssue")
    private String KPSIssue;

    @ColumnInfo(name = "created_at")
    private String created_at;

    @ColumnInfo(name = "updated_at")
    private String updated_at;

    public TicketRepositories() {
    }

    public TicketRepositories(String id, String name, String description, String parentTicket, String type, String KPSCategory, String KPSIssue, String created_at, String updated_at) {
        this.id = id;
        Name = name;
        Description = description;
        ParentTicket = parentTicket;
        Type = type;
        this.KPSCategory = KPSCategory;
        this.KPSIssue = KPSIssue;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getParentTicket() {
        return ParentTicket;
    }

    public void setParentTicket(String parentTicket) {
        ParentTicket = parentTicket;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getKPSCategory() {
        return KPSCategory;
    }

    public void setKPSCategory(String KPSCategory) {
        this.KPSCategory = KPSCategory;
    }

    public String getKPSIssue() {
        return KPSIssue;
    }

    public void setKPSIssue(String KPSIssue) {
        this.KPSIssue = KPSIssue;
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
