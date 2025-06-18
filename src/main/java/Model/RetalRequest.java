/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.sql.Date;

/**
 *
 * @author ThanhTruc
 */
public class RetalRequest {
    private int requestID;
    private int customerID;
    private  int roomID;
    private Date requestDate;
    private String requestStatus;
    private int approvedBy;
    private Date approvedDate;

    public RetalRequest(int requestID, int customerID, int roomID, Date requestDate, String requestStatus, int approvedBy, Date approvedDate) {
        this.requestID = requestID;
        this.customerID = customerID;
        this.roomID = roomID;
        this.requestDate = requestDate;
        this.requestStatus = requestStatus;
        this.approvedBy = approvedBy;
        this.approvedDate = approvedDate;
    }

    public int getRequestID() {
        return requestID;
    }

    public void setRequestID(int requestID) {
        this.requestID = requestID;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public int getRoomID() {
        return roomID;
    }

    public void setRoomID(int roomID) {
        this.roomID = roomID;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    public int getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(int approvedBy) {
        this.approvedBy = approvedBy;
    }

    public Date getApprovedDate() {
        return approvedDate;
    }

    public void setApprovedDate(Date approvedDate) {
        this.approvedDate = approvedDate;
    }
    
}
