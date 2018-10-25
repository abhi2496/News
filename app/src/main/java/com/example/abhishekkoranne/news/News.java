package com.example.abhishekkoranne.news;

public class News {

    private String title;
    private String topic;
    private String author;
    private String url;
    private String date;

    public News(String title, String topic, String author, String url, String date) {
        this.title = title;
        this.topic = topic;
        this.author = author;
        this.url = url;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public String getTopic() {
        return topic;
    }

    public String getAuthor() {
        return author;
    }

    public String getUrl() {
        return url;
    }

    public String getDate() {
        return date;
    }
}
