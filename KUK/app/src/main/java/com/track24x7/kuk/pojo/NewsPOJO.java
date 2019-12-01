package com.track24x7.kuk.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NewsPOJO {

    @SerializedName("col_NewsID")
    @Expose
    private String col_NewsID;

    @SerializedName("col_NewsDateTime")
    @Expose
    private String col_NewsDateTime;

    @SerializedName("col_News")
    @Expose
    private String col_News;


    public String getNewsId() {
        return col_NewsID;
    }
    public void settNewsId(String col_NewsID) {
        this.col_NewsID = col_NewsID;
    }

    public String getNews() {
        return col_News;
    }
    public void setNews(String message) {
        this.col_News = col_News;
    }

    public String getNewsDateTime() {
        return col_NewsDateTime;
    }
    public void setNewsDateTime(String col_NewsDateTime) {
        this.col_NewsDateTime = col_NewsDateTime;
    }
}
