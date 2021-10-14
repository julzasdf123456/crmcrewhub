package com.lopez.julz.crmcrewhub.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ServiceConnectionInspections {
    @PrimaryKey
    @NonNull
    private String id;

    @ColumnInfo(name = "ServiceConnectionId")
    private String ServiceConnectionId;

    @ColumnInfo(name = "SEMainCircuitBreakerAsPlan")
    private String SEMainCircuitBreakerAsPlan;

    @ColumnInfo(name = "SEMainCircuitBreakerAsInstalled")
    private String SEMainCircuitBreakerAsInstalled;

    @ColumnInfo(name = "SENoOfBranchesAsPlan")
    private String SENoOfBranchesAsPlan;

    @ColumnInfo(name = "SENoOfBranchesAsInstalled")
    private String SENoOfBranchesAsInstalled;

    @ColumnInfo(name = "PoleNumber")
    private String PoleNumber;

    @ColumnInfo(name = "PoleGIEstimatedDiameter")
    private String PoleGIEstimatedDiameter;

    @ColumnInfo(name = "PoleGIHeight")
    private String PoleGIHeight;

    @ColumnInfo(name = "PoleGINoOfLiftPoles")
    private String PoleGINoOfLiftPoles;

    @ColumnInfo(name = "PoleConcreteEstimatedDiameter")
    private String PoleConcreteEstimatedDiameter;

    @ColumnInfo(name = "PoleConcreteHeight")
    private String PoleConcreteHeight;

    @ColumnInfo(name = "PoleConcreteNoOfLiftPoles")
    private String PoleConcreteNoOfLiftPoles;

    @ColumnInfo(name = "PoleHardwoodEstimatedDiameter")
    private String PoleHardwoodEstimatedDiameter;

    @ColumnInfo(name = "PoleHardwoodHeight")
    private String PoleHardwoodHeight;

    @ColumnInfo(name = "PoleHardwoodNoOfLiftPoles")
    private String PoleHardwoodNoOfLiftPoles;

    @ColumnInfo(name = "PoleRemarks")
    private String PoleRemarks;

    @ColumnInfo(name = "SDWSizeAsPlan")
    private String SDWSizeAsPlan;

    @ColumnInfo(name = "SDWSizeAsInstalled")
    private String SDWSizeAsInstalled;

    @ColumnInfo(name = "SDWLengthAsPlan")
    private String SDWLengthAsPlan;

    @ColumnInfo(name = "SDWLengthAsInstalled")
    private String SDWLengthAsInstalled;

    @ColumnInfo(name = "GeoBuilding")
    private String GeoBuilding;

    @ColumnInfo(name = "GeoTappingPole")
    private String GeoTappingPole;

    @ColumnInfo(name = "GeoMeteringPole")
    private String GeoMeteringPole;

    @ColumnInfo(name = "GeoSEPole")
    private String GeoSEPole;

    @ColumnInfo(name = "FirstNeighborName")
    private String FirstNeighborName;

    @ColumnInfo(name = "FirstNeighborMeterSerial")
    private String FirstNeighborMeterSerial;

    @ColumnInfo(name = "SecondNeighborName")
    private String SecondNeighborName;

    @ColumnInfo(name = "SecondNeighborMeterSerial")
    private String SecondNeighborMeterSerial;

    @ColumnInfo(name = "EngineerInchargeName")
    private String EngineerInchargeName;

    @ColumnInfo(name = "EngineerInchargeTitle")
    private String EngineerInchargeTitle;

    @ColumnInfo(name = "EngineerInchargeLicenseNo")
    private String EngineerInchargeLicenseNo;

    @ColumnInfo(name = "EngineerInchargeLicenseValidity")
    private String EngineerInchargeLicenseValidity;

    @ColumnInfo(name = "EngineerInchargeContactNo")
    private String EngineerInchargeContactNo;

    @ColumnInfo(name = "Status")
    private String Status;

    @ColumnInfo(name = "Inspector")
    private String Inspector;

    @ColumnInfo(name = "DateOfVerification")
    private String DateOfVerification;

    @ColumnInfo(name = "EstimatedDateForReinspection")
    private String EstimatedDateForReinspection;

    @ColumnInfo(name = "Notes")
    private String Notes;

    public ServiceConnectionInspections() {}

    public ServiceConnectionInspections(@NonNull String id, String serviceConnectionId, String SEMainCircuitBreakerAsPlan, String SEMainCircuitBreakerAsInstalled, String SENoOfBranchesAsPlan, String SENoOfBranchesAsInstalled, String poleNumber, String poleGIEstimatedDiameter, String poleGIHeight, String poleGINoOfLiftPoles, String poleConcreteEstimatedDiameter, String poleConcreteHeight, String poleConcreteNoOfLiftPoles, String poleHardwoodEstimatedDiameter, String poleHardwoodHeight, String poleHardwoodNoOfLiftPoles, String poleRemarks, String SDWSizeAsPlan, String SDWSizeAsInstalled, String SDWLengthAsPlan, String SDWLengthAsInstalled, String geoBuilding, String geoTappingPole, String geoMeteringPole, String geoSEPole, String firstNeighborName, String firstNeighborMeterSerial, String secondNeighborName, String secondNeighborMeterSerial, String engineerInchargeName, String engineerInchargeTitle, String engineerInchargeLicenseNo, String engineerInchargeLicenseValidity, String engineerInchargeContactNo, String status, String inspector, String dateOfVerification, String estimatedDateForReinspection, String notes) {
        this.id = id;
        ServiceConnectionId = serviceConnectionId;
        this.SEMainCircuitBreakerAsPlan = SEMainCircuitBreakerAsPlan;
        this.SEMainCircuitBreakerAsInstalled = SEMainCircuitBreakerAsInstalled;
        this.SENoOfBranchesAsPlan = SENoOfBranchesAsPlan;
        this.SENoOfBranchesAsInstalled = SENoOfBranchesAsInstalled;
        PoleNumber = poleNumber;
        PoleGIEstimatedDiameter = poleGIEstimatedDiameter;
        PoleGIHeight = poleGIHeight;
        PoleGINoOfLiftPoles = poleGINoOfLiftPoles;
        PoleConcreteEstimatedDiameter = poleConcreteEstimatedDiameter;
        PoleConcreteHeight = poleConcreteHeight;
        PoleConcreteNoOfLiftPoles = poleConcreteNoOfLiftPoles;
        PoleHardwoodEstimatedDiameter = poleHardwoodEstimatedDiameter;
        PoleHardwoodHeight = poleHardwoodHeight;
        PoleHardwoodNoOfLiftPoles = poleHardwoodNoOfLiftPoles;
        PoleRemarks = poleRemarks;
        this.SDWSizeAsPlan = SDWSizeAsPlan;
        this.SDWSizeAsInstalled = SDWSizeAsInstalled;
        this.SDWLengthAsPlan = SDWLengthAsPlan;
        this.SDWLengthAsInstalled = SDWLengthAsInstalled;
        GeoBuilding = geoBuilding;
        GeoTappingPole = geoTappingPole;
        GeoMeteringPole = geoMeteringPole;
        GeoSEPole = geoSEPole;
        FirstNeighborName = firstNeighborName;
        FirstNeighborMeterSerial = firstNeighborMeterSerial;
        SecondNeighborName = secondNeighborName;
        SecondNeighborMeterSerial = secondNeighborMeterSerial;
        EngineerInchargeName = engineerInchargeName;
        EngineerInchargeTitle = engineerInchargeTitle;
        EngineerInchargeLicenseNo = engineerInchargeLicenseNo;
        EngineerInchargeLicenseValidity = engineerInchargeLicenseValidity;
        EngineerInchargeContactNo = engineerInchargeContactNo;
        Status = status;
        Inspector = inspector;
        DateOfVerification = dateOfVerification;
        EstimatedDateForReinspection = estimatedDateForReinspection;
        Notes = notes;
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

    public String getSEMainCircuitBreakerAsPlan() {
        return SEMainCircuitBreakerAsPlan;
    }

    public void setSEMainCircuitBreakerAsPlan(String SEMainCircuitBreakerAsPlan) {
        this.SEMainCircuitBreakerAsPlan = SEMainCircuitBreakerAsPlan;
    }

    public String getSEMainCircuitBreakerAsInstalled() {
        return SEMainCircuitBreakerAsInstalled;
    }

    public void setSEMainCircuitBreakerAsInstalled(String SEMainCircuitBreakerAsInstalled) {
        this.SEMainCircuitBreakerAsInstalled = SEMainCircuitBreakerAsInstalled;
    }

    public String getSENoOfBranchesAsPlan() {
        return SENoOfBranchesAsPlan;
    }

    public void setSENoOfBranchesAsPlan(String SENoOfBranchesAsPlan) {
        this.SENoOfBranchesAsPlan = SENoOfBranchesAsPlan;
    }

    public String getSENoOfBranchesAsInstalled() {
        return SENoOfBranchesAsInstalled;
    }

    public void setSENoOfBranchesAsInstalled(String SENoOfBranchesAsInstalled) {
        this.SENoOfBranchesAsInstalled = SENoOfBranchesAsInstalled;
    }

    public String getPoleNumber() {
        return PoleNumber;
    }

    public void setPoleNumber(String poleNumber) {
        PoleNumber = poleNumber;
    }

    public String getPoleGIEstimatedDiameter() {
        return PoleGIEstimatedDiameter;
    }

    public void setPoleGIEstimatedDiameter(String poleGIEstimatedDiameter) {
        PoleGIEstimatedDiameter = poleGIEstimatedDiameter;
    }

    public String getPoleGIHeight() {
        return PoleGIHeight;
    }

    public void setPoleGIHeight(String poleGIHeight) {
        PoleGIHeight = poleGIHeight;
    }

    public String getPoleGINoOfLiftPoles() {
        return PoleGINoOfLiftPoles;
    }

    public void setPoleGINoOfLiftPoles(String poleGINoOfLiftPoles) {
        PoleGINoOfLiftPoles = poleGINoOfLiftPoles;
    }

    public String getPoleConcreteEstimatedDiameter() {
        return PoleConcreteEstimatedDiameter;
    }

    public void setPoleConcreteEstimatedDiameter(String poleConcreteEstimatedDiameter) {
        PoleConcreteEstimatedDiameter = poleConcreteEstimatedDiameter;
    }

    public String getPoleConcreteHeight() {
        return PoleConcreteHeight;
    }

    public void setPoleConcreteHeight(String poleConcreteHeight) {
        PoleConcreteHeight = poleConcreteHeight;
    }

    public String getPoleConcreteNoOfLiftPoles() {
        return PoleConcreteNoOfLiftPoles;
    }

    public void setPoleConcreteNoOfLiftPoles(String poleConcreteNoOfLiftPoles) {
        PoleConcreteNoOfLiftPoles = poleConcreteNoOfLiftPoles;
    }

    public String getPoleHardwoodEstimatedDiameter() {
        return PoleHardwoodEstimatedDiameter;
    }

    public void setPoleHardwoodEstimatedDiameter(String poleHardwoodEstimatedDiameter) {
        PoleHardwoodEstimatedDiameter = poleHardwoodEstimatedDiameter;
    }

    public String getPoleHardwoodHeight() {
        return PoleHardwoodHeight;
    }

    public void setPoleHardwoodHeight(String poleHardwoodHeight) {
        PoleHardwoodHeight = poleHardwoodHeight;
    }

    public String getPoleHardwoodNoOfLiftPoles() {
        return PoleHardwoodNoOfLiftPoles;
    }

    public void setPoleHardwoodNoOfLiftPoles(String poleHardwoodNoOfLiftPoles) {
        PoleHardwoodNoOfLiftPoles = poleHardwoodNoOfLiftPoles;
    }

    public String getPoleRemarks() {
        return PoleRemarks;
    }

    public void setPoleRemarks(String poleRemarks) {
        PoleRemarks = poleRemarks;
    }

    public String getSDWSizeAsPlan() {
        return SDWSizeAsPlan;
    }

    public void setSDWSizeAsPlan(String SDWSizeAsPlan) {
        this.SDWSizeAsPlan = SDWSizeAsPlan;
    }

    public String getSDWSizeAsInstalled() {
        return SDWSizeAsInstalled;
    }

    public void setSDWSizeAsInstalled(String SDWSizeAsInstalled) {
        this.SDWSizeAsInstalled = SDWSizeAsInstalled;
    }

    public String getSDWLengthAsPlan() {
        return SDWLengthAsPlan;
    }

    public void setSDWLengthAsPlan(String SDWLengthAsPlan) {
        this.SDWLengthAsPlan = SDWLengthAsPlan;
    }

    public String getSDWLengthAsInstalled() {
        return SDWLengthAsInstalled;
    }

    public void setSDWLengthAsInstalled(String SDWLengthAsInstalled) {
        this.SDWLengthAsInstalled = SDWLengthAsInstalled;
    }

    public String getGeoBuilding() {
        return GeoBuilding;
    }

    public void setGeoBuilding(String geoBuilding) {
        GeoBuilding = geoBuilding;
    }

    public String getGeoTappingPole() {
        return GeoTappingPole;
    }

    public void setGeoTappingPole(String geoTappingPole) {
        GeoTappingPole = geoTappingPole;
    }

    public String getGeoMeteringPole() {
        return GeoMeteringPole;
    }

    public void setGeoMeteringPole(String geoMeteringPole) {
        GeoMeteringPole = geoMeteringPole;
    }

    public String getGeoSEPole() {
        return GeoSEPole;
    }

    public void setGeoSEPole(String geoSEPole) {
        GeoSEPole = geoSEPole;
    }

    public String getFirstNeighborName() {
        return FirstNeighborName;
    }

    public void setFirstNeighborName(String firstNeighborName) {
        FirstNeighborName = firstNeighborName;
    }

    public String getFirstNeighborMeterSerial() {
        return FirstNeighborMeterSerial;
    }

    public void setFirstNeighborMeterSerial(String firstNeighborMeterSerial) {
        FirstNeighborMeterSerial = firstNeighborMeterSerial;
    }

    public String getSecondNeighborName() {
        return SecondNeighborName;
    }

    public void setSecondNeighborName(String secondNeighborName) {
        SecondNeighborName = secondNeighborName;
    }

    public String getSecondNeighborMeterSerial() {
        return SecondNeighborMeterSerial;
    }

    public void setSecondNeighborMeterSerial(String secondNeighborMeterSerial) {
        SecondNeighborMeterSerial = secondNeighborMeterSerial;
    }

    public String getEngineerInchargeName() {
        return EngineerInchargeName;
    }

    public void setEngineerInchargeName(String engineerInchargeName) {
        EngineerInchargeName = engineerInchargeName;
    }

    public String getEngineerInchargeTitle() {
        return EngineerInchargeTitle;
    }

    public void setEngineerInchargeTitle(String engineerInchargeTitle) {
        EngineerInchargeTitle = engineerInchargeTitle;
    }

    public String getEngineerInchargeLicenseNo() {
        return EngineerInchargeLicenseNo;
    }

    public void setEngineerInchargeLicenseNo(String engineerInchargeLicenseNo) {
        EngineerInchargeLicenseNo = engineerInchargeLicenseNo;
    }

    public String getEngineerInchargeLicenseValidity() {
        return EngineerInchargeLicenseValidity;
    }

    public void setEngineerInchargeLicenseValidity(String engineerInchargeLicenseValidity) {
        EngineerInchargeLicenseValidity = engineerInchargeLicenseValidity;
    }

    public String getEngineerInchargeContactNo() {
        return EngineerInchargeContactNo;
    }

    public void setEngineerInchargeContactNo(String engineerInchargeContactNo) {
        EngineerInchargeContactNo = engineerInchargeContactNo;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getInspector() {
        return Inspector;
    }

    public void setInspector(String inspector) {
        Inspector = inspector;
    }

    public String getDateOfVerification() {
        return DateOfVerification;
    }

    public void setDateOfVerification(String dateOfVerification) {
        DateOfVerification = dateOfVerification;
    }

    public String getEstimatedDateForReinspection() {
        return EstimatedDateForReinspection;
    }

    public void setEstimatedDateForReinspection(String estimatedDateForReinspection) {
        EstimatedDateForReinspection = estimatedDateForReinspection;
    }

    public String getNotes() {
        return Notes;
    }

    public void setNotes(String notes) {
        Notes = notes;
    }
}
