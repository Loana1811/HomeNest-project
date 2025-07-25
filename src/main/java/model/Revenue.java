/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDate;

/**
 *
 * @author Admin
 */
public class Revenue {

    private int revenueID;
    private String revenueName;
    private double amount;
    private LocalDate revenueDate;
    private String payer;
    private String notes;
    private String categoryName; // tên danh mục thu

    private int revenueCategoryID;

    public Revenue() {
    }

    public Revenue(int revenueID, String revenueName, double amount, LocalDate revenueDate, String payer, String notes, String categoryName, int revenueCategoryID) {
        this.revenueID = revenueID;
        this.revenueName = revenueName;
        this.amount = amount;
        this.revenueDate = revenueDate;
        this.payer = payer;
        this.notes = notes;
        this.categoryName = categoryName;
        this.revenueCategoryID = revenueCategoryID;
    }

    public LocalDate getRevenueDate() {
        return revenueDate;
    }

    public void setRevenueDate(LocalDate revenueDate) {
        this.revenueDate = revenueDate;
    }

    public int getRevenueID() {
        return revenueID;
    }

    public void setRevenueID(int revenueID) {
        this.revenueID = revenueID;
    }

    public String getRevenueName() {
        return revenueName;
    }

    public void setRevenueName(String revenueName) {
        this.revenueName = revenueName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPayer() {
        return payer;
    }

    public void setPayer(String payer) {
        this.payer = payer;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getRevenueCategoryID() {
        return revenueCategoryID;
    }

    public void setRevenueCategoryID(int revenueCategoryID) {
        this.revenueCategoryID = revenueCategoryID;
    }
}
