package com.track24x7.kuk.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RequirementPOJO {

    @SerializedName("col_RequirementID")
    @Expose
    private String col_RequirementID;

    @SerializedName("col_RequirementDescription")
    @Expose
    private String RequirementDescription;
    @SerializedName("col_Expired")
    @Expose
    private String expired;
    @SerializedName("col_RequirementPostedDateTime")
    @Expose
    private String col_RequirementPostedDateTime;
    @SerializedName("col_RequirementCategory")
    @Expose
    private String col_RequirementCategory;
    @SerializedName("col_RequirementContact")
    @Expose
    private String col_RequirementContact;

    @SerializedName("col_RequirementEmail")
    @Expose
    private String col_RequirementEmail;
    @SerializedName("col_PostedBy")
    @Expose
    private String col_PostedBy;
    @SerializedName("col_PostedById")
    @Expose
    private String col_PostedById;
    @SerializedName("col_RequirementPostedDate")
    @Expose
    private String col_RequirementPostedDate;

    public String getRequirementId() {
        return col_RequirementID;
    }

    public void setRequirementId(String RequirementID) {
        this.col_RequirementID = RequirementID;
    }

    public String getRequirementDescription() {
        return RequirementDescription;
    }

    public void setRequirementDescription(String RequirementDescription) {
        this.RequirementDescription = RequirementDescription;
    }
    public String getCategory() {
        return col_RequirementCategory;
    }
    public void setCategory(String category) {
        this.col_RequirementCategory = category;
    }
    public String getContactNumber() {
        return col_RequirementContact;
    }
    public void setContactNumber(String contactNumber) {
        this.col_RequirementContact = contactNumber;
    }

    public String getEmail() {
        return col_RequirementEmail;
    }
    public void setEmail(String contactEmail) {
        this.col_RequirementEmail = contactEmail;
    }
    public String getPostedById() {
        return col_PostedBy;
    }
    public void serPostedById(String postedById) {
        this.col_PostedBy = postedById;
    }
    public String getPostedBy() {
        return col_PostedBy;
    }
    public void serPostedBy(String postedBy) {
        this.col_PostedBy = postedBy;
    }
    public String getPostedOn() {
        return col_RequirementPostedDate;
    }
    public void serPostedOn(String postDateTime) {
        this.col_RequirementPostedDate = postDateTime;
    }
}

