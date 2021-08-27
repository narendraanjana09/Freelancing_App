package com.nsa.labelimages.Model;

import android.graphics.Bitmap;

public class ImagesModel {
    private String name;
    private String imageLink;
    private String textLink;
    private Bitmap imgBitmap;

    public ImagesModel(String name, String imageLink, String textLink, Bitmap imgBitmap) {
        this.name = name;
        this.imageLink = imageLink;
        this.textLink = textLink;
        this.imgBitmap = imgBitmap;
    }

    public Bitmap getImgBitmap() {
        return imgBitmap;
    }

    public void setImgBitmap(Bitmap imgBitmap) {
        this.imgBitmap = imgBitmap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getTextLink() {
        return textLink;
    }

    public void setTextLink(String textLink) {
        this.textLink = textLink;
    }
}
