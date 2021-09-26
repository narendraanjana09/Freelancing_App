package com.nsa.flexjobs.Model;

import android.net.Uri;

public class ImageNameModel{
    private String name;
    private Uri link;

    public ImageNameModel(String name, Uri link) {
        this.name = name;
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Uri getLink() {
        return link;
    }

    public void setLink(Uri link) {
        this.link = link;
    }
}

