package com.nsa.flexjobs.Message;

public class Chat {

    private String sender;
    private String receiver;
    private String message;
    private String date;
    private String time;
    private boolean isseen;



    public Chat() {
    }

    public Chat(String sender, String receiver, String message, String date, String time, boolean isseen) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.date = date;
        this.time = time;
        this.isseen = isseen;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
