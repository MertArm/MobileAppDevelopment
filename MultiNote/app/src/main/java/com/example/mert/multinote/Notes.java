package com.example.mert.multinote;

import java.io.Serializable;
import java.util.Date;

public class Notes implements Serializable{
    private String title;
    private String description;
    private String date;

    public Notes(String title, String description, String date) {
        this.title = title;
        this.description = description;
        this.date = date;
    }

    public String getTitle(){
        return this.title;
    }

    public String getDescription(){
        return this.description;
    }

    public String getDate(){
        return this.date;
    }

}
