package com.nsa.flexjobs.Model;

public class TaskModel {
    private String task_type;
    private String user_id;
    private String user_name;
    private String prev_img_link;
    private String task_id;
    private String task_name;
    private String task_note;
    private String task_value;
    private String posted_date;
    private String totalTestImages;
    private String totalMainImages;

    public TaskModel() {
    }

    public TaskModel(String task_type, String user_id, String user_name, String prev_img_link, String task_id, String task_name, String task_note, String task_value, String posted_date, String totalTestImages, String totalMainImages) {
        this.task_type = task_type;
        this.user_id = user_id;
        this.user_name = user_name;
        this.prev_img_link = prev_img_link;
        this.task_id = task_id;
        this.task_name = task_name;
        this.task_note = task_note;
        this.task_value = task_value;
        this.posted_date = posted_date;
        this.totalTestImages = totalTestImages;
        this.totalMainImages = totalMainImages;
    }

    public String getTask_type() {
        return task_type;
    }

    public void setTask_type(String task_type) {
        this.task_type = task_type;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getPrev_img_link() {
        return prev_img_link;
    }

    public void setPrev_img_link(String prev_img_link) {
        this.prev_img_link = prev_img_link;
    }

    public String getTask_id() {
        return task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    public String getTask_name() {
        return task_name;
    }

    public void setTask_name(String task_name) {
        this.task_name = task_name;
    }

    public String getTask_note() {
        return task_note;
    }

    public void setTask_note(String task_note) {
        this.task_note = task_note;
    }

    public String getTask_value() {
        return task_value;
    }

    public void setTask_value(String task_value) {
        this.task_value = task_value;
    }

    public String getPosted_date() {
        return posted_date;
    }

    public void setPosted_date(String posted_date) {
        this.posted_date = posted_date;
    }

    public String getTotalTestImages() {
        return totalTestImages;
    }

    public void setTotalTestImages(String totalTestImages) {
        this.totalTestImages = totalTestImages;
    }

    public String getTotalMainImages() {
        return totalMainImages;
    }

    public void setTotalMainImages(String totalMainImages) {
        this.totalMainImages = totalMainImages;
    }
}
