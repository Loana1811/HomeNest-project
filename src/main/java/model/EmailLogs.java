package model;

import java.sql.Timestamp;

public class EmailLogs {
    private int emailLogID;
    private Integer customerID; // Nullable
    private Integer userID; // Nullable
    private Integer sentByUserID; // Nullable, tracks who sent the email
    private String email;
    private String subject;
    private String message;
    private Timestamp sentAt;
    private String status; // Sent or Failed
    private String errorMessage; // Nullable

    // Default constructor
    public EmailLogs() {
    }

    // Full constructor
    public EmailLogs(int emailLogID, Integer customerID, Integer userID, Integer sentByUserID, String email, 
                     String subject, String message, Timestamp sentAt, String status, String errorMessage) {
        this.emailLogID = emailLogID;
        this.customerID = customerID;
        this.userID = userID;
        this.sentByUserID = sentByUserID;
        this.email = email;
        this.subject = subject;
        this.message = message;
        this.sentAt = sentAt;
        this.status = status;
        this.errorMessage = errorMessage;
    }

    // Getters and Setters
    public int getEmailLogID() {
        return emailLogID;
    }

    public void setEmailLogID(int emailLogID) {
        this.emailLogID = emailLogID;
    }

    public Integer getCustomerID() {
        return customerID;
    }

    public void setCustomerID(Integer customerID) {
        this.customerID = customerID;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public Integer getSentByUserID() {
        return sentByUserID;
    }

    public void setSentByUserID(Integer sentByUserID) {
        this.sentByUserID = sentByUserID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getSentAt() {
        return sentAt;
    }

    public void setSentAt(Timestamp sentAt) {
        this.sentAt = sentAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}