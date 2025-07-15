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
public class Expense {

    private int expenseID;
    private String expenseName;
    private double amount;
    private LocalDate expenseDate;
    private String payer;
    private String notes;
    private String categoryName;
    private int expenseCategoryID; // ✅ Thêm dòng này

    public Expense() {
    }

    public Expense(int expenseID, String expenseName, double amount, LocalDate expenseDate, String payer, String notes, String categoryName, int expenseCategoryID) {
        this.expenseID = expenseID;
        this.expenseName = expenseName;
        this.amount = amount;
        this.expenseDate = expenseDate;
        this.payer = payer;
        this.notes = notes;
        this.categoryName = categoryName;
        this.expenseCategoryID = expenseCategoryID;
    }

    public int getExpenseID() {
        return expenseID;
    }

    public void setExpenseID(int expenseID) {
        this.expenseID = expenseID;
    }

    public String getExpenseName() {
        return expenseName;
    }

    public void setExpenseName(String expenseName) {
        this.expenseName = expenseName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDate getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(LocalDate expenseDate) {
        this.expenseDate = expenseDate;
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

    public int getExpenseCategoryID() { // ✅ Đã sửa
        return expenseCategoryID;
    }

    public void setExpenseCategoryID(int expenseCategoryID) { // ✅ Đã sửa
        this.expenseCategoryID = expenseCategoryID;
    }
}
