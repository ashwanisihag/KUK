package com.track24x7.kuk.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class UserListPOJO implements Serializable{
    @SerializedName("$id")
    @Expose
    private String id_1;
    @SerializedName("AspNetUserClaims")
    @Expose
    private List<Object> aspNetUserClaims = null;
    @SerializedName("AspNetUserLogins")
    @Expose
    private List<Object> aspNetUserLogins = null;
    @SerializedName("tbl_Photo")
    @Expose
    private List<Object> tblPhoto = null;
    @SerializedName("AspNetRoles")
    @Expose
    private List<Object> aspNetRoles = null;
    @SerializedName("Id")
    @Expose
    private String id;
    @SerializedName("School")
    @Expose
    private String school;
    @SerializedName("FirstName")
    @Expose
    private String firstName;
    @SerializedName("LastName")
    @Expose
    private String lastName;
    @SerializedName("Email")
    @Expose
    private String email;
    @SerializedName("EmailConfirmed")
    @Expose
    private Boolean emailConfirmed;
    @SerializedName("PasswordHash")
    @Expose
    private String passwordHash;
    @SerializedName("SecurityStamp")
    @Expose
    private String securityStamp;
    @SerializedName("PhoneNumber")
    @Expose
    private String phoneNumber;
    @SerializedName("PhoneNumberConfirmed")
    @Expose
    private Boolean phoneNumberConfirmed;
    @SerializedName("TwoFactorEnabled")
    @Expose
    private Boolean twoFactorEnabled;
    @SerializedName("LockoutEndDateUtc")
    @Expose
    private Object lockoutEndDateUtc;
    @SerializedName("LockoutEnabled")
    @Expose
    private Boolean lockoutEnabled;
    @SerializedName("AccessFailedCount")
    @Expose
    private Integer accessFailedCount;
    @SerializedName("UserName")
    @Expose
    private String userName;
    @SerializedName("Address")
    @Expose
    private String address;
    @SerializedName("DateOfBirth")
    @Expose
    private String dateOfBirth;
    @SerializedName("City")
    @Expose
    private String city;
    @SerializedName("State")
    @Expose
    private String state;
    @SerializedName("PostalCode")
    @Expose
    private String postalCode;
    /*@SerializedName("RollNo")
    @Expose
    private Integer rollNo;*/
    @SerializedName("JoiningYear")
    @Expose
    private String joiningYear;
    @SerializedName("LeavingYear")
    @Expose
    private String leavingYear;
    @SerializedName("Designation")
    @Expose
    private String designation;
    @SerializedName("Posting")
    @Expose
    private String posting;
    @SerializedName("BloodGroup")
    @Expose
    private String bloodGroup;
    @SerializedName("Latitude")
    @Expose
    private Double latitude;
    @SerializedName("Longitude")
    @Expose
    private Double longitude;
    @SerializedName("House")
    @Expose
    private String house;
    @SerializedName("Department")
    @Expose
    private String Department;
    @SerializedName("ProfileLink")
    @Expose
    private String ProfileLink;
    @SerializedName("PhoneVisible")
    @Expose
    private boolean PhoneVisible;
    @SerializedName("ShowLocation")
    @Expose
    private boolean ShowLocation;
    @SerializedName("Profession")
    @Expose
    private String Profession;
   /* public String getId_1() {
        return id_1;
    }

    public void setId_1(String id_1) {
        this.id_1 = id_1;
    }*/

    public List<Object> getAspNetUserClaims() {
        return aspNetUserClaims;
    }

    public void setAspNetUserClaims(List<Object> aspNetUserClaims) {
        this.aspNetUserClaims = aspNetUserClaims;
    }

    public List<Object> getAspNetUserLogins() {
        return aspNetUserLogins;
    }

    public void setAspNetUserLogins(List<Object> aspNetUserLogins) {
        this.aspNetUserLogins = aspNetUserLogins;
    }

    public List<Object> getTblPhoto() {
        return tblPhoto;
    }

    public void setTblPhoto(List<Object> tblPhoto) {
        this.tblPhoto = tblPhoto;
    }

    public List<Object> getAspNetRoles() {
        return aspNetRoles;
    }

    public void setAspNetRoles(List<Object> aspNetRoles) {
        this.aspNetRoles = aspNetRoles;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getEmailConfirmed() {
        return emailConfirmed;
    }

    public void setEmailConfirmed(Boolean emailConfirmed) {
        this.emailConfirmed = emailConfirmed;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getSecurityStamp() {
        return securityStamp;
    }

    public void setSecurityStamp(String securityStamp) {
        this.securityStamp = securityStamp;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Boolean getPhoneNumberConfirmed() {
        return phoneNumberConfirmed;
    }

    public void setPhoneNumberConfirmed(Boolean phoneNumberConfirmed) {
        this.phoneNumberConfirmed = phoneNumberConfirmed;
    }

    public Boolean getTwoFactorEnabled() {
        return twoFactorEnabled;
    }

    public void setTwoFactorEnabled(Boolean twoFactorEnabled) {
        this.twoFactorEnabled = twoFactorEnabled;
    }

    public Object getLockoutEndDateUtc() {
        return lockoutEndDateUtc;
    }

    public void setLockoutEndDateUtc(Object lockoutEndDateUtc) {
        this.lockoutEndDateUtc = lockoutEndDateUtc;
    }

    public Boolean getLockoutEnabled() {
        return lockoutEnabled;
    }

    public void setLockoutEnabled(Boolean lockoutEnabled) {
        this.lockoutEnabled = lockoutEnabled;
    }

    public Integer getAccessFailedCount() {
        return accessFailedCount;
    }

    public void setAccessFailedCount(Integer accessFailedCount) {
        this.accessFailedCount = accessFailedCount;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /*public Integer getRollNo() {
        return rollNo;
    }

    public void setRollNo(Integer rollNo) {
        this.rollNo = rollNo;
    }*/

    public String getJoiningYear() {
        return joiningYear;
    }

    public void setJoiningYear(String joiningYear) {
        this.joiningYear = joiningYear;
    }

    public String getLeavingYear() {
        return leavingYear;
    }

    public void setLeavingYear(String leavingYear) {
        this.leavingYear = leavingYear;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getPosting() {
        return posting;
    }

    public void setPosting(String posting) {
        this.posting = posting;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public String getProfession() {
        return Profession;
    }

    public void setProfession(String profession) {
        this.Profession = profession;
    }


    public String getProfileLink() {
        return ProfileLink;
    }
    public void setProfileLink(String profileLink) {
        this.ProfileLink = profileLink;
    }


    public String getDepartment() {
        return Department;
    }

    public void setDepartment(String department) {
        Department = department;
    }
    public String getSchool() {
        return school;
    }
    public void setSchool(String school) {
        this.school = school;
    }
    public boolean getPhoneVisible() {
        return PhoneVisible;
    }
    public void setPhoneVisible(boolean phoneVisible) {
        this.PhoneVisible = phoneVisible;
    }
    public boolean getLocationVisible() {
        return ShowLocation;
    }
    public void setLocationVisible(boolean showLocation) {
        this.ShowLocation = showLocation;
    }
}
