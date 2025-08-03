/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Date;
import java.util.Base64;

/**
 *
 * @author kloane
 */
public class Room {

    private int roomID;
    private String roomNumber;

    private double rentPrice;
    private double area;
    private String location;
    private String roomStatus;
    private int blockID;
    private byte[] imagePath;
    private String description;
    private Date postedDate;
    private String activeContractCode; // Hợp đồng đang hoạt động/tháng này
    private boolean hasRecord;         // Đã có utility record chưa?
    private boolean hasBill;           // Đã lập bill chưa?
    private int isTrashFree;
    private int isWifiFree;
    private int isWaterFree;
    private int isElectricityFree;

    // Constructors
    public Room() {
    }

    public Room(int roomID, String roomNumber, double rentPrice, double area, String location, String roomStatus, int blockID, byte[] imagePath, String description, Date postedDate, String activeContractCode, boolean hasRecord, boolean hasBill, int isTrashFree, int isWifiFree, int isWaterFree, int isElectricityFree) {
        this.roomID = roomID;
        this.roomNumber = roomNumber;
        this.rentPrice = rentPrice;
        this.area = area;
        this.location = location;
        this.roomStatus = roomStatus;
        this.blockID = blockID;
        this.imagePath = imagePath;
        this.description = description;
        this.postedDate = postedDate;
        this.activeContractCode = activeContractCode;
        this.hasRecord = hasRecord;
        this.hasBill = hasBill;
        this.isTrashFree = isTrashFree;
        this.isWifiFree = isWifiFree;
        this.isWaterFree = isWaterFree;
        this.isElectricityFree = isElectricityFree;
    }

    public Room(int roomID, String roomNumber, float rentPrice, float area, String location, String roomStatus, int blockID, byte[] imagePath, String description, Date postedDate) {
        this.roomID = roomID;
        this.roomNumber = roomNumber;
        this.rentPrice = rentPrice;
        this.area = area;
        this.location = location;
        this.roomStatus = roomStatus;
        this.blockID = blockID;
        this.imagePath = imagePath;
        this.description = description;
        this.postedDate = postedDate;
    }

    public Room(int roomID, String roomNumber, double rentPrice, double area, String location,
            String roomStatus, int blockID,
            byte[] imagePath, String description, Date postedDate) {
        this.roomID = roomID;
        this.roomNumber = roomNumber;
        this.rentPrice = rentPrice;
        this.area = area;
        this.location = location;
        this.roomStatus = roomStatus;
        this.blockID = blockID;
        this.imagePath = imagePath;
        this.description = description;
        this.postedDate = postedDate;
    }

    // Getters and Setters
    public int getRoomID() {
        return roomID;
    }

    public void setRoomID(int roomID) {
        this.roomID = roomID;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public double getRentPrice() {
        return rentPrice;
    }

    public void setRentPrice(double rentPrice) {
        this.rentPrice = rentPrice;
    }

    public double getArea() {
        return area;
    }

    public String getActiveContractCode() {
        return activeContractCode;
    }

    public void setActiveContractCode(String code) {
        this.activeContractCode = code;
    }

    public boolean isHasRecord() {
        return hasRecord;
    }

    public void setHasRecord(boolean hasRecord) {
        this.hasRecord = hasRecord;
    }

    public boolean isHasBill() {
        return hasBill;
    }

    public void setHasBill(boolean hasBill) {
        this.hasBill = hasBill;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRoomStatus() {
        return roomStatus;
    }

    public void setRoomStatus(String roomStatus) {
        this.roomStatus = roomStatus;
    }

    public int getBlockID() {
        return blockID;
    }

    public void setBlockID(int blockID) {
        this.blockID = blockID;
    }

    public byte[] getImagePath() {
        return imagePath;
    }

    public void setImagePath(byte[] imagePath) {
        this.imagePath = imagePath;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(Date postedDate) {
        this.postedDate = postedDate;
    }

    public int getIsTrashFree() {
        return isTrashFree;
    }

    public void setIsTrashFree(int isTrashFree) {
        this.isTrashFree = isTrashFree;
    }

    public int getIsWifiFree() {
        return isWifiFree;
    }

    public void setIsWifiFree(int isWifiFree) {
        this.isWifiFree = isWifiFree;
    }

    public int getIsWaterFree() {
        return isWaterFree;
    }

    public void setIsWaterFree(int isWaterFree) {
        this.isWaterFree = isWaterFree;
    }

    public int getIsElectricityFree() {
        return isElectricityFree;
    }

    public void setIsElectricityFree(int isElectricityFree) {
        this.isElectricityFree = isElectricityFree;
    }

    @Override
    public String toString() {
        return "Room{"
                + "roomID=" + roomID
                + ", roomNumber='" + roomNumber + '\''
                + ", rentPrice=" + rentPrice
                + ", area=" + area
                + ", location='" + location + '\''
                + ", roomStatus='" + roomStatus + '\''
                + ", blockID=" + blockID
                + ", imagePath='" + imagePath + '\''
                + ", description='" + description + '\''
                + ", postedDate=" + postedDate
                + ", activeContractCode='" + activeContractCode + '\''
                + ", hasRecord=" + hasRecord
                + ", hasBill=" + hasBill
                + ", isTrashFree=" + isTrashFree
                + ", isWifiFree=" + isWifiFree
                + ", isWaterFree=" + isWaterFree
                + ", isElectricityFree=" + isElectricityFree
                + '}';
    }

    public String getImageBase64() {
        if (imagePath != null && imagePath.length > 0) {
            return Base64.getEncoder().encodeToString(imagePath);
        }
        return null;
    }

}
