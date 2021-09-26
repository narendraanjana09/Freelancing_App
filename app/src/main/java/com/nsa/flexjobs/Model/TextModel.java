package com.nsa.flexjobs.Model;

import java.util.List;

public class TextModel {
    private String name;
    private List<FormatModel> text_list;

    public TextModel(String name, List<FormatModel> text_list) {
        this.name = name;
        this.text_list = text_list;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<FormatModel> getText_list() {
        return text_list;
    }

    public void setText_list(List<FormatModel> text_list) {
        this.text_list = text_list;
    }
}
