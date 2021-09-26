package com.nsa.flexjobs.Model;

public class RealImagesModel {
    private String name;
    private String link;
    private ImageEditedModel editedModel;

    public RealImagesModel() {
    }

    public RealImagesModel(String name, String link, ImageEditedModel editedModel) {
        this.name = name;
        this.link = link;
        this.editedModel = editedModel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public ImageEditedModel getEditedModel() {
        return editedModel;
    }

    public void setEditedModel(ImageEditedModel editedModel) {
        this.editedModel = editedModel;
    }
}
