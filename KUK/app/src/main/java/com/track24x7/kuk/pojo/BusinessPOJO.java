package com.track24x7.kuk.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BusinessPOJO {

    @SerializedName("col_BusinessID")
    @Expose
    private String col_BusinessID;

    @SerializedName("col_JobTitle")
    @Expose
    private String col_JobTitle;

    @SerializedName("col_BusinessName")
    @Expose
    private String col_BusinessName;

    @SerializedName("col_BusinessCategory")
    @Expose
    private String col_BusinessCategory;

    @SerializedName("col_BusinessDescription")
    @Expose
    private String col_BusinessDescription;

    @SerializedName("col_BusinessContact")
    @Expose
    private String col_BusinessContact;

    @SerializedName("col_BusinessEmail")
    @Expose
    private String col_BusinessEmail;

    @SerializedName("col_PostedBy")
    @Expose
    private String postedBy;

    @SerializedName("col_PostedById")
    @Expose
    private String postedById;

    public String getBusinessId() {
        return col_BusinessID;
    }
    public void setBusinessId(String col_BusinessID) {
        this.col_BusinessID = col_BusinessID;
    }

    public String getBusinessName() {
        return col_BusinessName;
    }
    public void setBusinessName(String col_BusinessName) {
        this.col_BusinessName = col_BusinessName;
    }

    public String getBusinessDescription() {
        return col_BusinessDescription;
    }
    public void setBusinessDescription(String col_BusinessDescription) {
        this.col_BusinessDescription = col_BusinessDescription;
    }
    public String geBusinesstCategory() {
        return col_BusinessCategory;
    }
    public void setBusinessCategory(String col_BusinessCategory) {
        this.col_BusinessCategory = col_BusinessCategory;
    }

    public String getBusinessContact() {
        return col_BusinessContact;
    }
    public void setBusinessContact(String col_BusinessContact) {
        this.col_BusinessContact = col_BusinessContact;
    }

    public String getBusinessEmail() {
        return col_BusinessEmail;
    }
    public void setBusinessEmail(String col_BusinessEmail) {
        this.col_BusinessEmail = col_BusinessEmail;
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

}

