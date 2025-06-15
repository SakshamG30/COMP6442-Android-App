package com.example.g11_group_application.Service_layer;

/**
 * @Author: Saksham Gupta (u7726995)
 * Created: 15-May-2024
 * Represents unread message details of a particular sender
 * along with the timestamp and excrypted message ID
 */
public class UnreadMessageDetails {
    private String senderId;
    private String senderName;
    private String messageSnippet;
    private long timestamp;
    private String messageId;

    public UnreadMessageDetails() {
    }
    public UnreadMessageDetails(String senderId, String senderName, String messageSnippet, long timestamp, String messageId) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.messageSnippet = messageSnippet;
        this.timestamp = timestamp;
        this.messageId = messageId;
    }

    // Getters and Setters
    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName(){
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getMessageSnippet() {
        return messageSnippet;
    }

    public void setMessageSnippet(String messageSnippet) {
        this.messageSnippet = messageSnippet;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}
