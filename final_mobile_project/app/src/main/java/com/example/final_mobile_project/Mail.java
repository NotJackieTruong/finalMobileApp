package com.example.final_mobile_project;

public class Mail {
    private String subject;
    private String content;
    private String from;
    private String to;
    private String date;
    private String id;

    public Mail(String subject, String content, String from, String to, String date, String id) {
        this.subject = subject;
        this.content = content;
        this.from = from;
        this.to = to;
        this.date = date;
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
