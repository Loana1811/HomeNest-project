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
    private BigDecimal roomRent; // Changed from float to BigDecimal for consistency
    private BigDecimal deposit;
    private String phone;
    private float area;
    private String location;
    private int blockId;
    private String description;
    private boolean isElectricityFree;
    private boolean isWaterFree;
    private boolean isWifiFree;
    private boolean isTrashFree;

    // Default constructor
    public Contract() {
    }

    // Constructor for retrieving contracts with minimal fields
    public Contract(int contractId, int tenantId, int roomId, java.util.Date startDate, java.util.Date endDate,
                    String contractStatus, java.util.Date contractCreatedAt) {
        this.contractId = contractId;
        this.tenantId = tenantId;
        this.roomId = roomId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.contractStatus = contractStatus;
        this.contractCreatedAt = contractCreatedAt;
    }

    // Constructor with all fields
    public Contract(int contractId, int tenantId, String tenantName, int roomId, String roomNumber,
                    java.util.Date startDate, java.util.Date endDate, String contractStatus,
                    java.util.Date contractCreatedAt, BigDecimal roomRent, BigDecimal deposit, String phone,
                    float area, String location, int blockId, String description,
                    boolean isElectricityFree, boolean isWaterFree, boolean isWifiFree, boolean isTrashFree) {
        this.contractId = contractId;
        this.tenantId = tenantId;
        this.tenantName = tenantName;
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.startDate = startDate;
        this.endDate = endDate;
        this.contractStatus = contractStatus;
        this.contractCreatedAt = contractCreatedAt;
        this.roomRent = roomRent;
        this.deposit = deposit;
        this.phone = phone;
        this.area = area;
        this.location = location;
        this.blockId = blockId;
        this.description = description;
        this.isElectricityFree = isElectricityFree;
        this.isWaterFree = isWaterFree;
        this.isWifiFree = isWifiFree;
        this.isTrashFree = isTrashFree;
    }

    // Getters and Setters
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

    public BigDecimal getRoomRent() {
        return roomRent;
    }

    public void setRoomRent(BigDecimal roomRent) {
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

    public float getArea() {
        return area;
    }

    public void setArea(float area) {
        this.area = area;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getBlockId() {
        return blockId;
    }

    public void setBlockId(int blockId) {
        this.blockId = blockId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isElectricityFree() {
        return isElectricityFree;
    }

    public void setElectricityFree(boolean isElectricityFree) {
        this.isElectricityFree = isElectricityFree;
    }

    public boolean isWaterFree() {
        return isWaterFree;
    }

    public void setWaterFree(boolean isWaterFree) {
        this.isWaterFree = isWaterFree;
    }

    public boolean isWifiFree() {
        return isWifiFree;
    }

    public void setWifiFree(boolean isWifiFree) {
        this.isWifiFree = isWifiFree;
    }

    public boolean isTrashFree() {
        return isTrashFree;
    }

    public void setTrashFree(boolean isTrashFree) {
        this.isTrashFree = isTrashFree;
    }

    public int getTenantID() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}