/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Timestamp;

/**
 * Model class representing a PaymentConfirmation record in the PaymentConfirmations table.
 * @author ThanhTruc
 */
public class PaymentConfirmation {
    private int confirmationID;
    private int paymentID;
    private int tenantID;
    private byte[] imageData;
    private Timestamp createdAt;

    // Constructor
    public PaymentConfirmation() {
    }

    public PaymentConfirmation(int confirmationID, int paymentID, int tenantID, byte[] imageData, Timestamp createdAt) {
        this.confirmationID = confirmationID;
        this.paymentID = paymentID;
        this.tenantID = tenantID;
        this.imageData = imageData;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getConfirmationID() {
        return confirmationID;
    }

    public void setConfirmationID(int confirmationID) {
        this.confirmationID = confirmationID;
    }

    public int getPaymentID() {
        return paymentID;
    }

    public void setPaymentID(int paymentID) {
        this.paymentID = paymentID;
    }

    public int getTenantID() {
        return tenantID;
    }

    public void setTenantID(int tenantID) {
        this.tenantID = tenantID;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}