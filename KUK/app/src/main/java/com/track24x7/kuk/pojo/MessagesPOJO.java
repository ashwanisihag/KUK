package com.track24x7.kuk.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MessagesPOJO {

    @SerializedName("MessageID")
    @Expose
    private String messageID;

    @SerializedName("From")
    @Expose
    private String from;

    @SerializedName("Message")
    @Expose
    private String message;

    @SerializedName("UserID")
    @Expose
    private String userID;

    public String getMessageId() {
        return messageID;
    }

    public void setMessageId(String messageID) {
        this.messageID = messageID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public String getUserID() {
        return userID;
    }
    public void setUserID(String userID) {
        this.userID = userID;
    }
    public String getFrom() {
        return from;
    }
    public void serFrom(String from) {
        this.from = from;
    }
}
