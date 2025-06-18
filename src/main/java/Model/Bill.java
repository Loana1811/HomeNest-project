/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Date;

/**
 *
 * @author ThanhTruc
 */
public class Bill {
    private int billID;
    private int contractID;
    private Date BillDate;
    private float totalAmount;
    private String billStatus;

    public Bill(int billID, int contractID, Date BillDate, float totalAmount, String billStatus) {
        this.billID = billID;
        this.contractID = contractID;
        this.BillDate = BillDate;
        this.totalAmount = totalAmount;
        this.billStatus = billStatus;
    }

    public int getBillID() {
        return billID;
    }

    public void setBillID(int billID) {
        this.billID = billID;
    }

    public int getContractID() {
        return contractID;
    }

    public void setContractID(int contractID) {
        this.contractID = contractID;
    }

    public Date getBillDate() {
        return BillDate;
    }

    public void setBillDate(Date BillDate) {
        this.BillDate = BillDate;
    }

    public float getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(float totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getBillStatus() {
        return billStatus;
    }

    public void setBillStatus(String billStatus) {
        this.billStatus = billStatus;
    }
    
}
