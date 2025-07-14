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
    private int contractID;
    private int tenantID;
    private int roomID;
    private Date startDate;
    private Date endDate;
    private String contractStatus;
    private Date contractCreatedAt;

    public Contract(int contractID, int tenantID, int roomID, Date startDate, Date endDate, String contractStatus, Date contractCreatedAt) {
        this.contractID = contractID;
        this.tenantID = tenantID;
        this.roomID = roomID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.contractStatus = contractStatus;
        this.contractCreatedAt = contractCreatedAt;
    }

    public Contract() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public int getContractID() {
        return contractID;
    }

    public void setContractID(int contractID) {
        this.contractID = contractID;
    }

    public int getTenantID() {
        return tenantID;
    }

    public void setTenantID(int tenantID) {
        this.tenantID = tenantID;
    }

    public int getRoomID() {
        return roomID;
    }

    public void setRoomID(int roomID) {
        this.roomID = roomID;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getContractStatus() {
        return contractStatus;
    }

    public void setContractStatus(String contractStatus) {
        this.contractStatus = contractStatus;
    }

    public Date getContractCreatedAt() {
        return contractCreatedAt;
    }

    public void setContractCreatedAt(Date contractCreatedAt) {
        this.contractCreatedAt = contractCreatedAt;
    }
    
}
