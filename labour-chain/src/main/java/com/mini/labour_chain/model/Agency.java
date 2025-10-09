package com.mini.labour_chain.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "agency")
public class Agency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @Column(name = "license_number", unique = true)
    private String licenseNumber;

    @Column(name = "proprietor_name")
    private String proprietorName;

    @Column(name = "agency_name")
    private String agencyName;

    @Column(name = "contact_number")
    private String contactNumber;

    private String address;
    private String password;

    @Column(name = "profile_photo")
    private String profilePhoto;

    private String bio;

    private String status = "Pending";

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public String getProprietorName() { return proprietorName; }
    public void setProprietorName(String proprietorName) { this.proprietorName = proprietorName; }

    public String getAgencyName() { return agencyName; }
    public void setAgencyName(String agencyName) { this.agencyName = agencyName; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getProfilePhoto() { return profilePhoto; }
    public void setProfilePhoto(String profilePhoto) { this.profilePhoto = profilePhoto; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // Overriding equals() and hashCode() for proper object comparison
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Agency agency = (Agency) o;
        return Objects.equals(id, agency.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
