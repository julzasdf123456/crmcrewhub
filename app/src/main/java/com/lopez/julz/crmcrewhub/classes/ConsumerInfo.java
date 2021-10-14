package com.lopez.julz.crmcrewhub.classes;

public class ConsumerInfo {
    private String name, barangay, town;

    public ConsumerInfo() {}

    public ConsumerInfo(String name, String barangay, String town) {
        this.name = name;
        this.barangay = barangay;
        this.town = town;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBarangay() {
        return barangay;
    }

    public void setBarangay(String barangay) {
        this.barangay = barangay;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }
}
