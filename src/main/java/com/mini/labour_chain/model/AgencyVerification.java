package com.mini.labour_chain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class AgencyVerification {

    @Id
    private String licenseNumber;   // primary key

    private String proprietorName;
    private String contactNumber;
    private String address;

    // ✅ Getters and Setters
    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public String getProprietorName() { return proprietorName; }
    public void setProprietorName(String proprietorName) { this.proprietorName = proprietorName; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}
