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

//    public UtilityReading(int readingID, int utilityTypeID, int roomID, Date readingDate,
//                          BigDecimal oldReading, BigDecimal newReading,
//                          BigDecimal priceUsed, BigDecimal oldPrice,
//                          String changedBy, Timestamp utilityReadingCreatedAt) {
//        this.readingID = readingID;
//        this.utilityTypeID = utilityTypeID;
//        this.roomID = roomID;
//        this.readingDate = readingDate;
//        this.oldReading = oldReading;
//        this.newReading = newReading;
//        this.priceUsed = priceUsed;
//        this.oldPrice = oldPrice;
//        this.changedBy = changedBy;
//        this.utilityReadingCreatedAt = utilityReadingCreatedAt;
//    }
public UtilityReading(int readingID, int roomID, int utilityTypeID,
                      BigDecimal oldReading, BigDecimal newReading,
                      BigDecimal priceUsed, String changedBy, Date readingDate) {
    this.readingID = readingID;
    this.roomID = roomID;
    this.utilityTypeID = utilityTypeID;
    this.oldReading = oldReading;
    this.newReading = newReading;
    this.priceUsed = priceUsed;
    this.changedBy = changedBy;
    this.readingDate = readingDate;
    this.utilityReadingCreatedAt = new Timestamp(System.currentTimeMillis());
}

   
//    public UtilityReading(int readingID, int roomID, int utilityTypeID, double oldIndex, double newIndex,
//                      BigDecimal priceUsed, String changedBy, Date readingDate) {
//    this.readingID = readingID;
//    this.roomID = roomID;
//    this.utilityTypeID = utilityTypeID;
//    this.oldReading = BigDecimal.valueOf(oldIndex);
//    this.newReading = BigDecimal.valueOf(newIndex);
//    this.priceUsed = BigDecimal.valueOf(priceUsed);
//    this.changedBy = changedBy;
//    this.readingDate = readingDate;
//}

    public UtilityReading(int readingID, int utilityTypeID, int roomID, Date readingDate, BigDecimal oldReading, BigDecimal newReading, BigDecimal oldPrice, String changedBy) {
        this.readingID = readingID;
        this.utilityTypeID = utilityTypeID;
        this.roomID = roomID;
        this.readingDate = readingDate;
        this.oldReading = oldReading;
        this.newReading = newReading;
        this.oldPrice = oldPrice;
        this.changedBy = changedBy;
    }

    public UtilityReading(int i, int roomId, int typeId, BigDecimal oldIndex, double doubleValue, BigDecimal priceUsed, String admin, Date date) {
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
