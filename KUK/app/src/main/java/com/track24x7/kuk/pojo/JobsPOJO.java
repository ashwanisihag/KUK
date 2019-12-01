package com.track24x7.kuk.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class JobsPOJO {

    @SerializedName("col_JobID")
    @Expose
    private String jobID;
    @SerializedName("col_JobTitle")
    @Expose
    private String jobTitle;

    @SerializedName("col_JobDescription")
    @Expose
    private String jobDescription;
    @SerializedName("col_Expired")
    @Expose
    private String expired;
    @SerializedName("col_PostDateTime")
    @Expose
    private String postDateTime;
    @SerializedName("col_Category")
    @Expose
    private String category;
    @SerializedName("col_ContactNumber")
    @Expose
    private String contactNumber;

    @SerializedName("col_ContactEmail")
    @Expose
    private String contactEmail;
    @SerializedName("col_PostedBy")
    @Expose
    private String postedBy;
    @SerializedName("col_PostedById")
    @Expose
    private String postedById;

    public String getJobId() {
        return jobID;
    }

    public void setJobId(String jobID) {
        this.jobID = jobID;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public String getContactNumber() {
        return contactNumber;
    }
    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getEmail() {
        return contactEmail;
    }
    public void setEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }
    public String getPostedById() {
        return postedById;
    }
    public void serPostedById(String postedById) {
        this.postedBy = postedById;
    }
    public String getPostedBy() {
        return postedBy;
    }
    public void serPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }
    public String getPostedOn() {
        return postDateTime;
    }
    public void serPostedOn(String postDateTime) {
        this.postDateTime = postDateTime;
    }
}

