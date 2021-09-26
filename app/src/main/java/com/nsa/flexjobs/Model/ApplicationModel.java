package com.nsa.flexjobs.Model;

public class ApplicationModel {
    private String task_type;
    private String task_id;
    private String task_name;
    private String appli_date;
    private String task_post_date;
    private String verify_date;
    private String task_value;
    private String task_status;
    private String task_des;

    public ApplicationModel() {
    }

    public ApplicationModel(String task_type, String task_id, String task_name, String appli_date, String task_post_date, String verify_date, String task_value, String task_status, String task_des) {
        this.task_type = task_type;
        this.task_id = task_id;
        this.task_name = task_name;
        this.appli_date = appli_date;
        this.task_post_date = task_post_date;
        this.verify_date = verify_date;
        this.task_value = task_value;
        this.task_status = task_status;
        this.task_des = task_des;
    }

    public String getTask_des() {
        return task_des;
    }

    public void setTask_des(String task_des) {
        this.task_des = task_des;
    }

    public String getTask_type() {
        return task_type;
    }

    public void setTask_type(String task_type) {
        this.task_type = task_type;
    }

    public String getVerify_date() {
        return verify_date;
    }

    public void setVerify_date(String verify_date) {
        this.verify_date = verify_date;
    }

    public String getTask_post_date() {
        return task_post_date;
    }

    public void setTask_post_date(String task_post_date) {
        this.task_post_date = task_post_date;
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

    public String getAppli_date() {
        return appli_date;
    }

    public void setAppli_date(String appli_date) {
        this.appli_date = appli_date;
    }

    public String getTask_value() {
        return task_value;
    }

    public void setTask_value(String task_value) {
        this.task_value = task_value;
    }

    public String getTask_status() {
        return task_status;
    }

    public void setTask_status(String task_status) {
        this.task_status = task_status;
    }
}
