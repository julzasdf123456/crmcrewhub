package com.lopez.julz.crmcrewhub.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ServiceConnections {
    @PrimaryKey
    @NonNull
    private String id;

    @ColumnInfo(name = "MemberConsumerId")
    private String MemberConsumerId;

    @ColumnInfo(name = "DateOfApplication")
    private String DateOfApplication;

    @ColumnInfo(name = "ServiceAccountName")
    private String ServiceAccountName;

    @ColumnInfo(name = "AccountCount")
    private String AccountCount;

    @ColumnInfo(name = "Sitio")
    private String Sitio;

    @ColumnInfo(name = "Barangay")
    private String Barangay;

    @ColumnInfo(name = "Town")
    private String Town;

    @ColumnInfo(name = "ContactNumber")
    private String ContactNumber;

    @ColumnInfo(name = "EmailAddress")
    private String EmailAddress;

    @ColumnInfo(name = "AccountType")
    private String AccountType;

    @ColumnInfo(name = "AccountOrganization")
    private String AccountOrganization;

    @ColumnInfo(name = "OrganizationAccountNumber")
    private String OrganizationAccountNumber;

    @ColumnInfo(name = "IsNIHE")
    private String IsNIHE;

    @ColumnInfo(name = "AccountApplicationType")
    private String AccountApplicationType;

    @ColumnInfo(name = "ConnectionApplicationType")
    private String ConnectionApplicationType;

    @ColumnInfo(name = "BuildingType")
    private String BuildingType;

    @ColumnInfo(name = "Status")
    private String Status;

    @ColumnInfo(name = "Notes")
    private String Notes;

    @ColumnInfo(name = "Trash")
    private String Trash;

    @ColumnInfo(name = "ORNumber")
    private String ORNumber;

    @ColumnInfo(name = "ORDate")
    private String ORDate;

    @ColumnInfo(name = "DateTimeLinemenArrived")
    private String DateTimeLinemenArrived;

    @ColumnInfo(name = "DateTimeOfEnergization")
    private String DateTimeOfEnergization;

    @ColumnInfo(name = "EnergizationOrderIssued")
    private String EnergizationOrderIssued;

    @ColumnInfo(name = "DateTimeOfEnergizationIssue")
    private String DateTimeOfEnergizationIssue;

    @ColumnInfo(name = "StationCrewAssigned")
    private String StationCrewAssigned;

    @ColumnInfo(name = "LoadCategory")
    private String LoadCategory;

    @ColumnInfo(name = "TemporaryDurationInMonths")
    private String TemporaryDurationInMonths;

    @ColumnInfo(name = "LongSpan")
    private String LongSpan;

    public ServiceConnections() {}

    public ServiceConnections(@NonNull String id, String memberConsumerId, String dateOfApplication, String serviceAccountName, String accountCount, String sitio, String barangay, String town, String contactNumber, String emailAddress, String accountType, String accountOrganization, String organizationAccountNumber, String isNIHE, String accountApplicationType, String connectionApplicationType, String buildingType, String status, String notes, String trash, String ORNumber, String ORDate, String dateTimeLinemenArrived, String dateTimeOfEnergization, String energizationOrderIssued, String dateTimeOfEnergizationIssue, String stationCrewAssigned, String loadCategory, String temporaryDurationInMonths, String longSpan) {
        this.id = id;
        MemberConsumerId = memberConsumerId;
        DateOfApplication = dateOfApplication;
        ServiceAccountName = serviceAccountName;
        AccountCount = accountCount;
        Sitio = sitio;
        Barangay = barangay;
        Town = town;
        ContactNumber = contactNumber;
        EmailAddress = emailAddress;
        AccountType = accountType;
        AccountOrganization = accountOrganization;
        OrganizationAccountNumber = organizationAccountNumber;
        IsNIHE = isNIHE;
        AccountApplicationType = accountApplicationType;
        ConnectionApplicationType = connectionApplicationType;
        BuildingType = buildingType;
        Status = status;
        Notes = notes;
        Trash = trash;
        this.ORNumber = ORNumber;
        this.ORDate = ORDate;
        DateTimeLinemenArrived = dateTimeLinemenArrived;
        DateTimeOfEnergization = dateTimeOfEnergization;
        EnergizationOrderIssued = energizationOrderIssued;
        DateTimeOfEnergizationIssue = dateTimeOfEnergizationIssue;
        StationCrewAssigned = stationCrewAssigned;
        LoadCategory = loadCategory;
        TemporaryDurationInMonths = temporaryDurationInMonths;
        LongSpan = longSpan;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getMemberConsumerId() {
        return MemberConsumerId;
    }

    public void setMemberConsumerId(String memberConsumerId) {
        MemberConsumerId = memberConsumerId;
    }

    public String getDateOfApplication() {
        return DateOfApplication;
    }

    public void setDateOfApplication(String dateOfApplication) {
        DateOfApplication = dateOfApplication;
    }

    public String getServiceAccountName() {
        return ServiceAccountName;
    }

    public void setServiceAccountName(String serviceAccountName) {
        ServiceAccountName = serviceAccountName;
    }

    public String getAccountCount() {
        return AccountCount;
    }

    public void setAccountCount(String accountCount) {
        AccountCount = accountCount;
    }

    public String getSitio() {
        return Sitio;
    }

    public void setSitio(String sitio) {
        Sitio = sitio;
    }

    public String getBarangay() {
        return Barangay;
    }

    public void setBarangay(String barangay) {
        Barangay = barangay;
    }

    public String getTown() {
        return Town;
    }

    public void setTown(String town) {
        Town = town;
    }

    public String getContactNumber() {
        return ContactNumber;
    }

    public void setContactNumber(String contactNumber) {
        ContactNumber = contactNumber;
    }

    public String getEmailAddress() {
        return EmailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        EmailAddress = emailAddress;
    }

    public String getAccountType() {
        return AccountType;
    }

    public void setAccountType(String accountType) {
        AccountType = accountType;
    }

    public String getAccountOrganization() {
        return AccountOrganization;
    }

    public void setAccountOrganization(String accountOrganization) {
        AccountOrganization = accountOrganization;
    }

    public String getOrganizationAccountNumber() {
        return OrganizationAccountNumber;
    }

    public void setOrganizationAccountNumber(String organizationAccountNumber) {
        OrganizationAccountNumber = organizationAccountNumber;
    }

    public String getIsNIHE() {
        return IsNIHE;
    }

    public void setIsNIHE(String isNIHE) {
        IsNIHE = isNIHE;
    }

    public String getAccountApplicationType() {
        return AccountApplicationType;
    }

    public void setAccountApplicationType(String accountApplicationType) {
        AccountApplicationType = accountApplicationType;
    }

    public String getConnectionApplicationType() {
        return ConnectionApplicationType;
    }

    public void setConnectionApplicationType(String connectionApplicationType) {
        ConnectionApplicationType = connectionApplicationType;
    }

    public String getBuildingType() {
        return BuildingType;
    }

    public void setBuildingType(String buildingType) {
        BuildingType = buildingType;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getNotes() {
        return Notes;
    }

    public void setNotes(String notes) {
        Notes = notes;
    }

    public String getTrash() {
        return Trash;
    }

    public void setTrash(String trash) {
        Trash = trash;
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

    public String getEnergizationOrderIssued() {
        return EnergizationOrderIssued;
    }

    public void setEnergizationOrderIssued(String energizationOrderIssued) {
        EnergizationOrderIssued = energizationOrderIssued;
    }

    public String getDateTimeLinemenArrived() {
        return DateTimeLinemenArrived;
    }

    public void setDateTimeLinemenArrived(String dateTimeLinemenArrived) {
        DateTimeLinemenArrived = dateTimeLinemenArrived;
    }

    public String getDateTimeOfEnergization() {
        return DateTimeOfEnergization;
    }

    public void setDateTimeOfEnergization(String dateTimeOfEnergization) {
        DateTimeOfEnergization = dateTimeOfEnergization;
    }

    public String getDateTimeOfEnergizationIssue() {
        return DateTimeOfEnergizationIssue;
    }

    public void setDateTimeOfEnergizationIssue(String dateTimeOfEnergizationIssue) {
        DateTimeOfEnergizationIssue = dateTimeOfEnergizationIssue;
    }

    public String getStationCrewAssigned() {
        return StationCrewAssigned;
    }

    public void setStationCrewAssigned(String stationCrewAssigned) {
        StationCrewAssigned = stationCrewAssigned;
    }

    public String getLoadCategory() {
        return LoadCategory;
    }

    public void setLoadCategory(String loadCategory) {
        LoadCategory = loadCategory;
    }

    public String getTemporaryDurationInMonths() {
        return TemporaryDurationInMonths;
    }

    public void setTemporaryDurationInMonths(String temporaryDurationInMonths) {
        TemporaryDurationInMonths = temporaryDurationInMonths;
    }

    public String getLongSpan() {
        return LongSpan;
    }

    public void setLongSpan(String longSpan) {
        LongSpan = longSpan;
    }
}
