/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Admin
 */
import java.sql.Timestamp;

public class ExpenseCategory {

    private int expenseCategoryID;
    private String categoryName;
    private Timestamp createdAt;

    public ExpenseCategory() {
    }

    public ExpenseCategory(int expenseCategoryID, String categoryName, Timestamp createdAt) {
        this.expenseCategoryID = expenseCategoryID;
        this.categoryName = categoryName;
        this.createdAt = createdAt;
    }

    public int getExpenseCategoryID() {
        return expenseCategoryID;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setExpenseCategoryID(int expenseCategoryID) {
        this.expenseCategoryID = expenseCategoryID;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

}
