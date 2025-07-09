/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.math.BigDecimal;
import java.sql.Date;

/**
 *
 * @author ThanhTruc
 */
public class Contract {

    private int contractId;
    private int tenantId;
    private String tenantName;
    private int roomId;
    private String roomNumber;
    private java.util.Date startDate;
    private java.util.Date endDate;
    private String contractstatus;
    private java.util.Date contractcreatedAt;
    private double amount;
    private float roomRent;
    private BigDecimal deposit;
    private String phone;

    public Contract() {
    }

    public Contract(int contractId, int tenantId, String tenantName, int roomId, String roomNumber, java.util.Date startDate, java.util.Date endDate, String contractstatus, java.util.Date contractcreatedAt, double amount, float roomRent) {
        this.contractId = contractId;
        this.tenantId = tenantId;
        this.tenantName = tenantName;
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.startDate = startDate;
        this.endDate = endDate;
        this.contractstatus = contractstatus;
        this.contractcreatedAt = contractcreatedAt;
        this.amount = amount;
        this.roomRent = roomRent;
    }
    
    
    public Contract(int contractId, int tenantId, int roomId, java.util.Date startDate, java.util.Date endDate, String contractstatus, java.util.Date contractcreatedAt) {
        this.contractId = contractId;
        this.tenantId = tenantId;
        this.roomId = roomId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.contractstatus = contractstatus;
        this.contractcreatedAt = contractcreatedAt;
    }
    
    public Contract(int aInt, int aInt0, int aInt1, Date date, Date date0, String string, Date date1) {
    }

    public Contract(int contractId, int tenantId, String tenantName, int roomId, String roomNumber, java.util.Date startDate, java.util.Date endDate, String contractstatus, java.util.Date contractcreatedAt, double amount, BigDecimal deposit, float roomRent) {
        this.contractId = contractId;
        this.tenantId = tenantId;
        this.tenantName = tenantName;
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.startDate = startDate;
        this.endDate = endDate;
        this.contractstatus = contractstatus;
        this.contractcreatedAt = contractcreatedAt;
        this.amount = amount;
        this.deposit = deposit;
        this.roomRent = roomRent;
    }

    public Contract(int contractId, int tenantId, String tenantName, int roomId, String roomNumber, java.util.Date startDate, java.util.Date endDate, String contractstatus, java.util.Date contractcreatedAt, double amount, BigDecimal deposit, float roomRent, String phone) {
        this.contractId = contractId;
        this.tenantId = tenantId;
        this.tenantName = tenantName;
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.startDate = startDate;
        this.endDate = endDate;
        this.contractstatus = contractstatus;
        this.contractcreatedAt = contractcreatedAt;
        this.amount = amount;
        this.deposit = deposit;
        this.roomRent = roomRent;
        this.phone = phone;
    }

    public int getContractId() {
        return contractId;
    }

    public void setContractId(int contractId) {
        this.contractId = contractId;
    }

    public int getTenantId() {
        return tenantId;
    }

    public void setTenantId(int tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public java.util.Date getStartDate() {
        return startDate;
    }

    public void setStartDate(java.util.Date startDate) {
        this.startDate = startDate;
    }

    public java.util.Date getEndDate() {
        return endDate;
    }

    public void setEndDate(java.util.Date endDate) {
        this.endDate = endDate;
    }

    public String getContractstatus() {
        return contractstatus;
    }

    public void setContractstatus(String contractstatus) {
        this.contractstatus = contractstatus;
    }

    public java.util.Date getContractcreatedAt() {
        return contractcreatedAt;
    }

    public void setContractcreatedAt(java.util.Date contractcreatedAt) {
        this.contractcreatedAt = contractcreatedAt;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public float getRoomRent() {
        return roomRent;
    }

    public void setRoomRent(float roomRent) {
        this.roomRent = roomRent;
    }

    public BigDecimal getDeposit() {
        return deposit;
    }

    public void setDeposit(BigDecimal deposit) {
        this.deposit = deposit;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    

}
