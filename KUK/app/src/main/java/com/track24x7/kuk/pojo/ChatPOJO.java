package com.track24x7.kuk.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChatPOJO {
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("msg")
    @Expose
    private String msg;

    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getMessage() {
        return msg;
    }
    public void setMessage(String msg) {
        this.msg = msg;
    }
    public String getFrom() {
        return name;
    }
    public void setFrom(String name) {
        this.name = name;
    }
}
