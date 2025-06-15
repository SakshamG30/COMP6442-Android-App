package com.example.g11_group_application.Service_layer;

/**
 * @Author: Saksham Gupta (u7726995)
 * Created: 15-May-2024
 * Represents a message with the key, message details, time
 * and the name of the person who sent it
 */
public class Message implements Comparable<Message>{
    String key;
    String message;
    String userName;
    String date;
    String time;
    private int messageNo;  // Added field to keep track of message order

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Message(String key, String message, String userName, String date, String time, int messageNo) {
        this.key = key;
        this.message = message;
        this.userName = userName;
        this.date = date;
        this.time = time;
        this.messageNo = messageNo;
    }

    public Message() {
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getMessageNo() {
        return messageNo;
    }

    public void setMessageNo(int messageNo) {
        this.messageNo = messageNo;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public int compareTo(Message other) {
        return Integer.compare(this.messageNo, other.messageNo);
    }
}
