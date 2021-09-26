package com.nsa.flexjobs.Adapters;

import com.nsa.flexjobs.Model.File;

import java.util.List;

public class ApplyOtherModel {
    private String user_name;
    private String date;
    private String description;
    private String user_id;
    private List<File> files;

    public ApplyOtherModel() {
    }

    public ApplyOtherModel(String user_name, String date, String description, String user_id, List<File> files) {
        this.user_name = user_name;
        this.date = date;
        this.description = description;
        this.user_id = user_id;
        this.files = files;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }
}
