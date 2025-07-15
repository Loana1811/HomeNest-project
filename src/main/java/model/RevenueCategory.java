/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Timestamp;

/**
 *
 * @author Admin
 */


public class RevenueCategory {

    private int revenueCategoryID;
    private String categoryName;
    private Timestamp createdAt;

    public RevenueCategory() {
    }

    public RevenueCategory(int id, String name) {
        this.revenueCategoryID = id;
        this.categoryName = name;
    }

    public int getRevenueCategoryID() {
        return revenueCategoryID;
    }

    public void setRevenueCategoryID(int revenueCategoryID) {
        this.revenueCategoryID = revenueCategoryID;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
