package model;

import java.math.BigDecimal;
import java.sql.Date;

public class Contract {

    private int contractId;
    private int tenantId;
    private String tenantName;
    private int roomId;
    private String roomNumber;
    private java.util.Date startDate;
    private java.util.Date endDate;
    private String contractStatus;
    private java.util.Date contractCreatedAt;
    private double amount;
    private float roomRent;
    private BigDecimal deposit;
    private String phone;

    public Contract() {
    }

    public Contract(int contractId, int tenantId, String tenantName, int roomId, String roomNumber,
                    java.util.Date startDate, java.util.Date endDate, String contractStatus,
                    java.util.Date contractCreatedAt, double amount, float roomRent) {
        this.contractId = contractId;
        this.tenantId = tenantId;
        this.tenantName = tenantName;
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.startDate = startDate;
        this.endDate = endDate;
        this.contractStatus = contractStatus;
        this.contractCreatedAt = contractCreatedAt;
        this.amount = amount;
        this.roomRent = roomRent;
    }

    public Contract(int contractId, int tenantId, int roomId,
                    java.util.Date startDate, java.util.Date endDate, String contractStatus, java.util.Date contractCreatedAt) {
        this.contractId = contractId;
        this.tenantId = tenantId;
        this.roomId = roomId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.contractStatus = contractStatus;
        this.contractCreatedAt = contractCreatedAt;
    }

    public Contract(int contractId, int tenantId, String tenantName, int roomId, String roomNumber,
                    java.util.Date startDate, java.util.Date endDate, String contractStatus,
                    java.util.Date contractCreatedAt, double amount, BigDecimal deposit, float roomRent) {
        this.contractId = contractId;
        this.tenantId = tenantId;
        this.tenantName = tenantName;
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.startDate = startDate;
        this.endDate = endDate;
        this.contractStatus = contractStatus;
        this.contractCreatedAt = contractCreatedAt;
        this.amount = amount;
        this.deposit = deposit;
        this.roomRent = roomRent;
    }

    public Contract(int contractId, int tenantId, String tenantName, int roomId, String roomNumber,
                    java.util.Date startDate, java.util.Date endDate, String contractStatus,
                    java.util.Date contractCreatedAt, double amount, BigDecimal deposit, float roomRent, String phone) {
        this.contractId = contractId;
        this.tenantId = tenantId;
        this.tenantName = tenantName;
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.startDate = startDate;
        this.endDate = endDate;
        this.contractStatus = contractStatus;
        this.contractCreatedAt = contractCreatedAt;
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

    public String getContractStatus() {
        return contractStatus;
    }

    public void setContractStatus(String contractStatus) {
        this.contractStatus = contractStatus;
    }

    public java.util.Date getContractCreatedAt() {
        return contractCreatedAt;
    }

    public void setContractCreatedAt(java.util.Date contractCreatedAt) {
        this.contractCreatedAt = contractCreatedAt;
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
