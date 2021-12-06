package com.lopez.julz.crmcrewhub.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Tickets {

    @PrimaryKey
    @NonNull
    private String id;

    @ColumnInfo(name = "AccountNumber")
    private String AccountNumber;

    @ColumnInfo(name = "ConsumerName")
    private String ConsumerName;

    @ColumnInfo(name = "Town")
    private String Town;

    @ColumnInfo(name = "Barangay")
    private String Barangay;

    @ColumnInfo(name = "Sitio")
    private String Sitio;

    @ColumnInfo(name = "Ticket")
    private String Ticket;

    @ColumnInfo(name = "Reason")
    private String Reason;

    @ColumnInfo(name = "ContactNumber")
    private String ContactNumber;

    @ColumnInfo(name = "ReportedBy")
    private String ReportedBy;

    @ColumnInfo(name = "ORNumber")
    private String ORNumber;

    @ColumnInfo(name = "ORDate")
    private String ORDate;

    @ColumnInfo(name = "GeoLocation")
    private String GeoLocation;

    @ColumnInfo(name = "Neighbor1")
    private String Neighbor1;

    @ColumnInfo(name = "Neighbor2")
    private String Neighbor2;

    @ColumnInfo(name = "Notes")
    private String Notes;

    @ColumnInfo(name = "Status")
    private String Status;

    @ColumnInfo(name = "DateTimeDownloaded")
    private String DateTimeDownloaded;

    @ColumnInfo(name = "DateTimeLinemanArrived")
    private String DateTimeLinemanArrived;

    @ColumnInfo(name = "DateTimeLinemanExecuted")
    private String DateTimeLinemanExecuted;

    @ColumnInfo(name = "UserId")
    private String UserId;

    @ColumnInfo(name = "CrewAssigned")
    private String CrewAssigned;

    @ColumnInfo(name = "created_at")
    private String created_at;

    @ColumnInfo(name = "updated_at")
    private String updated_at;

    @ColumnInfo(name = "Trash")
    private String Trash;

    @ColumnInfo(name = "Office")
    private String Office;

    public Tickets() {
    }

    public Tickets(@NonNull String id, String accountNumber, String consumerName, String town, String barangay, String sitio, String ticket, String reason, String contactNumber, String reportedBy, String ORNumber, String ORDate, String geoLocation, String neighbor1, String neighbor2, String notes, String status, String dateTimeDownloaded, String dateTimeLinemanArrived, String dateTimeLinemanExecuted, String userId, String crewAssigned, String created_at, String updated_at, String trash, String office) {
        this.id = id;
        AccountNumber = accountNumber;
        ConsumerName = consumerName;
        Town = town;
        Barangay = barangay;
        Sitio = sitio;
        Ticket = ticket;
        Reason = reason;
        ContactNumber = contactNumber;
        ReportedBy = reportedBy;
        this.ORNumber = ORNumber;
        this.ORDate = ORDate;
        GeoLocation = geoLocation;
        Neighbor1 = neighbor1;
        Neighbor2 = neighbor2;
        Notes = notes;
        Status = status;
        DateTimeDownloaded = dateTimeDownloaded;
        DateTimeLinemanArrived = dateTimeLinemanArrived;
        DateTimeLinemanExecuted = dateTimeLinemanExecuted;
        UserId = userId;
        CrewAssigned = crewAssigned;
        this.created_at = created_at;
        this.updated_at = updated_at;
        Trash = trash;
        Office = office;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return AccountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        AccountNumber = accountNumber;
    }

    public String getConsumerName() {
        return ConsumerName;
    }

    public void setConsumerName(String consumerName) {
        ConsumerName = consumerName;
    }

    public String getTown() {
        return Town;
    }

    public void setTown(String town) {
        Town = town;
    }

    public String getBarangay() {
        return Barangay;
    }

    public void setBarangay(String barangay) {
        Barangay = barangay;
    }

    public String getSitio() {
        return Sitio;
    }

    public void setSitio(String sitio) {
        Sitio = sitio;
    }

    public String getTicket() {
        return Ticket;
    }

    public void setTicket(String ticket) {
        Ticket = ticket;
    }

    public String getReason() {
        return Reason;
    }

    public void setReason(String reason) {
        Reason = reason;
    }

    public String getContactNumber() {
        return ContactNumber;
    }

    public void setContactNumber(String contactNumber) {
        ContactNumber = contactNumber;
    }

    public String getReportedBy() {
        return ReportedBy;
    }

    public void setReportedBy(String reportedBy) {
        ReportedBy = reportedBy;
    }

    public String getORNumber() {
        return ORNumber;
    }

    public void setORNumber(String ORNumber) {
        this.ORNumber = ORNumber;
    }

    public String getORDate() {
        return ORDate;
    }

    public void setORDate(String ORDate) {
        this.ORDate = ORDate;
    }

    public String getGeoLocation() {
        return GeoLocation;
    }

    public void setGeoLocation(String geoLocation) {
        GeoLocation = geoLocation;
    }

    public String getNeighbor1() {
        return Neighbor1;
    }

    public void setNeighbor1(String neighbor1) {
        Neighbor1 = neighbor1;
    }

    public String getNeighbor2() {
        return Neighbor2;
    }

    public void setNeighbor2(String neighbor2) {
        Neighbor2 = neighbor2;
    }

    public String getNotes() {
        return Notes;
    }

    public void setNotes(String notes) {
        Notes = notes;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getDateTimeDownloaded() {
        return DateTimeDownloaded;
    }

    public void setDateTimeDownloaded(String dateTimeDownloaded) {
        DateTimeDownloaded = dateTimeDownloaded;
    }

    public String getDateTimeLinemanArrived() {
        return DateTimeLinemanArrived;
    }

    public void setDateTimeLinemanArrived(String dateTimeLinemanArrived) {
        DateTimeLinemanArrived = dateTimeLinemanArrived;
    }

    public String getDateTimeLinemanExecuted() {
        return DateTimeLinemanExecuted;
    }

    public void setDateTimeLinemanExecuted(String dateTimeLinemanExecuted) {
        DateTimeLinemanExecuted = dateTimeLinemanExecuted;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getCrewAssigned() {
        return CrewAssigned;
    }

    public void setCrewAssigned(String crewAssigned) {
        CrewAssigned = crewAssigned;
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

    public String getTrash() {
        return Trash;
    }

    public void setTrash(String trash) {
        Trash = trash;
    }

    public String getOffice() {
        return Office;
    }

    public void setOffice(String office) {
        Office = office;
    }
}
