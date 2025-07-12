/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Timestamp;

/**
 *
 * @author ADMIN
 */
public class Notification {

    private int notificationID;
    private int customerID;
    private String title;
    private String message;
    private boolean isRead;
    private Timestamp notificationCreateAt;
    private String sentBy;

    public Notification() {
    }

    public Notification(int notificationID, int customerID, String title, String message, boolean isRead, Timestamp notificationCreateAt, String sentBy) {
        this.notificationID = notificationID;
        this.customerID = customerID;
        this.title = title;
        this.message = message;
        this.isRead = isRead;
        this.notificationCreateAt = notificationCreateAt;
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

    public boolean isIsRead() {
        return isRead;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }

    public Timestamp getNotificationCreateAt() {
        return notificationCreateAt;
    }

    public void setNotificationCreateAt(Timestamp notificationCreateAt) {
        this.notificationCreateAt = notificationCreateAt;
    }

    public String getSentBy() {
        return sentBy;
    }

    public void setSentBy(String sentBy) {
        this.sentBy = sentBy;
    }
}
