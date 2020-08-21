package com.example.apmah.Chat;

public class MessageClass {
    private String from,message,type;

    public MessageClass(String from, String message, String type) {
        this.from = from;
        this.message = message;
        this.type = type;
    }

    public MessageClass() {
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
