package com.example.messagingappexample;

public class Message {

    private String text;
    private String senderEmail;

    public Message(String text, String senderEmail) {
        this.text = text;
        this.senderEmail = senderEmail;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }
}
