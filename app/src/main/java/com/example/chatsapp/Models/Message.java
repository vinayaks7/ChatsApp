package com.example.chatsapp.Models;

public class Message {
    private String messageId;
    private String message;
    private String senderId;

    private String imageUrl;
    private int timestamp;
    private int feeling=-1;

    public Message() {
    }

    public Message(String messageId, String senderId, int timestamp) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.timestamp = timestamp;
    }
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public int getFeeling() {
        return feeling;
    }

    public void setFeeling(int feeling) {
        this.feeling = feeling;
    }
}
