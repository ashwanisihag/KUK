package com.track24x7.kuk.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NearByPOJO {
    @SerializedName("$id")
    @Expose
    private String $id;
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
    /*@SerializedName("RollNo")
    @Expose
    private String rollNo;*/
    @SerializedName("JoiningYear")
    @Expose
    private String joiningYear;
    @SerializedName("LeavingYear")
    @Expose
    private String leavingYear;
    @SerializedName("Distance")
    @Expose
    private Double distance;

    @SerializedName("Profession")
    @Expose
    private String profession;

    @SerializedName("Department")
    @Expose
    private String department;
    @SerializedName("Designation")
    @Expose
    private String designation;
    @SerializedName("PhoneNumber")
    @Expose
    private String phoneNumber;
    @SerializedName("City")
    @Expose
    private String city;
    @SerializedName("Posting")
    @Expose
    private String posting;

    @SerializedName("LocationDaysOld")
    @Expose
    private String locationDaysOld;

    public String get$id() {
        return $id;
    }

    public void set$id(String $id) {
        this.$id = $id;
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

    /*public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }*/

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getJoiningYear() {
        return joiningYear;
    }
    public String getLeavingYear() {
        return leavingYear;
    }
    public void setLeavingYear(String leavingYear) {
        this.leavingYear = leavingYear;
    }
    public void setJoiningYear(String joiningYear) {
        this.joiningYear = joiningYear;
    }
    public double getDistance() {
        return distance;
    }
    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getDepartment() {
        return department;
    }
    public String getProfession() {
        return profession;
    }
    public void setProfession(String profession) {
        this.profession = profession;
    }
    public void setDepartment(String department) {
        this.department = department;
    }
    public String getCity() {
        return city;
    }
    public void setPosting(String posting) {
        this.posting = posting;
    }
    public String getPosting() {
        return posting;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getDaysAgo() {
        return locationDaysOld;
    }
    public void setDaysAgo(String locationDaysOld) {
        this.locationDaysOld = locationDaysOld;
    }
    public String getSchool() {
        return school;
    }
    public void setSchool(String school) {
        this.school = school;
    }
}
