package com.nsa.flexjobs.Model;

import android.graphics.Bitmap;

import java.util.List;

public class ImageEditedModel {
    private String name;
    private Bitmap bitmap;
    private List<FormatModel> textList;

    public ImageEditedModel(String name, Bitmap bitmap, List<FormatModel> textList) {
        this.name = name;
        this.bitmap = bitmap;
        this.textList = textList;
    }

    public List<FormatModel> getTextList() {
        return textList;
    }

    public void setTextList(List<FormatModel> textList) {
        this.textList = textList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
