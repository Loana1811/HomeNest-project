package model;

import java.sql.Timestamp;

public class Notification {
    private int notificationID;
    private int customerID;
    private String title;
    private String message;
    private boolean isRead;
    private Timestamp notificationCreatedAt;
    private Integer sentBy;

    // Constructor mặc định
    public Notification() {
    }

    // Constructor đầy đủ
    public Notification(int notificationID, int customerID, String title, String message, boolean isRead, Timestamp notificationCreatedAt, Integer sentBy) {
        this.notificationID = notificationID;
        this.customerID = customerID;
        this.title = title;
        this.message = message;
        this.isRead = isRead;
        this.notificationCreatedAt = notificationCreatedAt;
        this.sentBy = sentBy;
    }

    // Getters và Setters
    public int getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(int notificationID) {
        this.notificationID = notificationID;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean getIsRead() { // Thay đổi từ isRead() thành getIsRead()
        return isRead;
    }

    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }

    public Timestamp getNotificationCreatedAt() {
        return notificationCreatedAt;
    }

    public void setNotificationCreatedAt(Timestamp notificationCreatedAt) {
        this.notificationCreatedAt = notificationCreatedAt;
    }

    public Integer getSentBy() {
        return sentBy;
    }

    public void setSentBy(Integer sentBy) {
        this.sentBy = sentBy;
    }
}