package com.example.mywhatsapp.Models;

public class ChatModel {

    public String image, username, lastmessage,receiverId;

    public ChatModel(String username, String image, String receiverId) {
        this.username = username;
        this.image = image;
        this.lastmessage = receiverId;
    }

    public ChatModel(String username, String image, String lastmessage, String receiverId) {
        this.username = username;
        this.image = image;
        this.lastmessage = lastmessage;
        this.receiverId = receiverId;
    }

    public ChatModel(String username, String image) {
        this.username = username;
        this.image = image;
    }

    public ChatModel(String username) {
        this.username = username;
    }
    public ChatModel(){}

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLastmessage() {
        return lastmessage;
    }

    public void setLastmessage(String lastmessage) {
        this.lastmessage = lastmessage;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }
}
