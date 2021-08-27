package com.nsa.labelimages.Model;

public class IDModel {
    private String id_type;
    private String id_number;

    public IDModel() {
    }

    public IDModel(String id_type, String id_number) {
        this.id_type = id_type;
        this.id_number = id_number;
    }

    public String getId_type() {
        return id_type;
    }

    public void setId_type(String id_type) {
        this.id_type = id_type;
    }

    public String getId_number() {
        return id_number;
    }

    public void setId_number(String id_number) {
        this.id_number = id_number;
    }
}
