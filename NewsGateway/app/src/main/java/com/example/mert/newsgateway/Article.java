package com.example.mert.newsgateway;
import java.io.Serializable;

public class Article implements Serializable {
    private String author;
    private String title;
    private String date;
    private String description;
    private String url;
    private String url2Image;
    private int index;

    public Article(String author, String title, String description, String url, String url2Image, String date, int index) {
        this.author = author;
        this.title = title;
        this.description = description;
        this.url = url;
        this.url2Image = url2Image;
        this.date = date;
        this.index = index;
    }

    public String getAuthor() {
        return this.author;
    }
    public String getTitle() {
        return this.title;
    }
    public String getDate() {
        return this.date;
    }
    public String getDescription() {
        return this.description;
    }
    public String getUrl() {
        return this.url;
    }
    public String getImageUrl() {
        return this.url2Image;
    }
    public int getIndex() {
        return this.index;
    }

}