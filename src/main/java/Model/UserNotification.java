package model;

import java.sql.Timestamp;

public class UserNotification {
    private int userNotificationID;
    private int userID;
    private String userTitle;
    private String userMessage;
    private boolean isRead;
    private Timestamp userNotificationCreatedAt;
    private Integer sentBy;

    // Default constructor
    public UserNotification() {
    }

    // Full constructor
    public UserNotification(int userNotificationID, int userID, String userTitle, String userMessage, boolean isRead, Timestamp userNotificationCreatedAt, Integer sentBy) {
        this.userNotificationID = userNotificationID;
        this.userID = userID;
        this.userTitle = userTitle;
        this.userMessage = userMessage;
        this.isRead = isRead;
        this.userNotificationCreatedAt = userNotificationCreatedAt;
        this.sentBy = sentBy;
    }

    // Getters and Setters
    public int getUserNotificationID() {
        return userNotificationID;
    }

    public void setUserNotificationID(int userNotificationID) {
        this.userNotificationID = userNotificationID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUserTitle() {
        return userTitle;
    }

    public void setUserTitle(String userTitle) {
        this.userTitle = userTitle;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }

    public Timestamp getUserNotificationCreatedAt() {
        return userNotificationCreatedAt;
    }

    public void setUserNotificationCreatedAt(Timestamp userNotificationCreatedAt) {
        this.userNotificationCreatedAt = userNotificationCreatedAt;
    }

    public Integer getSentBy() {
        return sentBy;
    }

    public void setSentBy(Integer sentBy) {
        this.sentBy = sentBy;
    }
}