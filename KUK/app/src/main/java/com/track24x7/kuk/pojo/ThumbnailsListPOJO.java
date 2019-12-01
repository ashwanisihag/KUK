package com.track24x7.kuk.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ThumbnailsListPOJO implements Serializable {
    @SerializedName("col_UserAlbumID")
    @Expose
    private String col_UserAlbumID;

    @SerializedName("col_Description")
    @Expose
    private String col_Description;

    @SerializedName("col_UserId")
    @Expose
    private String col_UserId;

    public String getId() {
        return col_UserAlbumID;
    }
    public void setId(String col_UserAlbumID) {
        this.col_UserAlbumID = col_UserAlbumID;
    }

    public String getPostedBy() {
        return col_UserId;
    }
    public void setPostedBy(String col_UserId) {
        this.col_UserId = col_UserId;
    }


    public String getDescription() {
        return col_Description;
    }
    public void setDescription(String col_Description) {
        this.col_Description = col_Description;
    }
}