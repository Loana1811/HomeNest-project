package model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

public class UtilityReading {

    private int readingID;
    private int utilityTypeID;      // FK → UtilityTypes
    private int roomID;             // FK → Rooms
    private Date readingDate;
    private BigDecimal oldReading;
    private BigDecimal newReading;
    private BigDecimal priceUsed;   // có thể null
    private BigDecimal oldPrice;    // có thể null
    private String changedBy;
    private Timestamp utilityReadingCreatedAt;

    public UtilityReading() {}

    public UtilityReading(int readingID, int utilityTypeID, int roomID, Date readingDate,
                          BigDecimal oldReading, BigDecimal newReading,
                          BigDecimal priceUsed, BigDecimal oldPrice,
                          String changedBy, Timestamp utilityReadingCreatedAt) {
        this.readingID = readingID;
        this.utilityTypeID = utilityTypeID;
        this.roomID = roomID;
        this.readingDate = readingDate;
        this.oldReading = oldReading;
        this.newReading = newReading;
        this.priceUsed = priceUsed;
        this.oldPrice = oldPrice;
        this.changedBy = changedBy;
        this.utilityReadingCreatedAt = utilityReadingCreatedAt;
    }

   

    public int getReadingID() {
        return readingID;
    }

    public void setReadingID(int readingID) {
        this.readingID = readingID;
    }

    public int getUtilityTypeID() {
        return utilityTypeID;
    }

    public void setUtilityTypeID(int utilityTypeID) {
        this.utilityTypeID = utilityTypeID;
    }

    public int getRoomID() {
        return roomID;
    }

    public void setRoomID(int roomID) {
        this.roomID = roomID;
    }

    public Date getReadingDate() {
        return readingDate;
    }

    public void setReadingDate(Date readingDate) {
        this.readingDate = readingDate;
    }

    public BigDecimal getOldReading() {
        return oldReading;
    }

    public void setOldReading(BigDecimal oldReading) {
        this.oldReading = oldReading;
    }

    public BigDecimal getNewReading() {
        return newReading;
    }

    public void setNewReading(BigDecimal newReading) {
        this.newReading = newReading;
    }

    public BigDecimal getPriceUsed() {
        return priceUsed;
    }

    public void setPriceUsed(BigDecimal priceUsed) {
        this.priceUsed = priceUsed;
    }

    public BigDecimal getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(BigDecimal oldPrice) {
        this.oldPrice = oldPrice;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }

    public Timestamp getUtilityReadingCreatedAt() {
        return utilityReadingCreatedAt;
    }

    public void setUtilityReadingCreatedAt(Timestamp utilityReadingCreatedAt) {
        this.utilityReadingCreatedAt = utilityReadingCreatedAt;
    }
}
