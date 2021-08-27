package com.nsa.labelimages.Model;

public class FormatModel {
    String LabelName;
    String X;
    String Y;
    String Height;
    String Width;

    public FormatModel(String labelName, String x, String y, String height, String width) {

        LabelName = labelName;
        X = x;
        Y = y;
        Height = height;
        Width = width;
    }
    public String getText(){
        return getLabelName()+" "+getX()+" "+getY()+" "+getWidth()+" "+getHeight();
    }

    public String getLabelName() {
        return LabelName;
    }

    public void setLabelName(String labelName) {
        LabelName = labelName;
    }

    public String getX() {
        return X;
    }

    public void setX(String x) {
        X = x;
    }

    public String getY() {
        return Y;
    }

    public void setY(String y) {
        Y = y;
    }

    public String getHeight() {
        return Height;
    }

    public void setHeight(String height) {
        Height = height;
    }

    public String getWidth() {
        return Width;
    }

    public void setWidth(String width) {
        Width = width;
    }
}
