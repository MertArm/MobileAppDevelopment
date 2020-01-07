package com.example.mert.newsgateway;

public class NewsSource {
    private String Id;
    private String name;
    private String description;
    private String url;
    private String category;

    public NewsSource(String Id, String name, String description, String url, String category) {
        this.Id = Id;
        this.name = name;
        this.description = description;
        this.url = url;
        this.category = category;
    }

    public String getId() { return this.Id; }
    public void setId(String s) { this.Id = s; }
    public String getName() {
        return this.name;
    }
    public String getCategory() {
        return this.category;
    }

}