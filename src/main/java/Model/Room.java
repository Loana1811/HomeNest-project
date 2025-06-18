/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.sql.Date;

/**
 *
 * @author kloane
 */
public class Room {
      private int roomID;
    private String roomNumber;
    private String roomType; // Có thể bỏ nếu không còn trong DB
    private double rentPrice;
    private double area;
    private String location;
    private String roomStatus;
    private int blockID;
    private int categoryID;
    private String highlights;
    private String imagePath;
    private String description;
    private Date postedDate;

    // Constructors
    public Room() {
    }

    public Room(int roomID, String roomNumber, double rentPrice, double area, String location,
            String roomStatus, int blockID, int categoryID,
            String highlights, String imagePath, String description, Date postedDate) {
        this.roomID = roomID;
        this.roomNumber = roomNumber;
        this.rentPrice = rentPrice;
        this.area = area;
        this.location = location;
        this.roomStatus = roomStatus;
        this.blockID = blockID;
        this.categoryID = categoryID;
        this.highlights = highlights;
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

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public String getHighlights() {
        return highlights;
    }

    public void setHighlights(String highlights) {
        this.highlights = highlights;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
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
   
    
    
}
