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

    public Contract() {
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

}
