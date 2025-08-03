
package model;

import java.math.BigDecimal;
import java.sql.Date;

public class UtilityHistoryView {
    private int utilityTypeId;
    private String utilityName;
    private BigDecimal oldPrice;
    private BigDecimal newPrice;
    private String changedBy;
    private Date createdAt;
    private Date applyAt;          // ngày áp dụng giá mới
    private boolean isScheduled;   // true nếu là giá đã lên lịch
    private boolean isUtility;
    private int blockID;

    public UtilityHistoryView(int utilityTypeId, String utilityName, BigDecimal oldPrice, BigDecimal newPrice, String changedBy, Date createdAt, int blockID) {
        this.utilityTypeId = utilityTypeId;
        this.utilityName = utilityName;
        this.oldPrice = oldPrice;
        this.newPrice = newPrice;
        this.changedBy = changedBy;
        this.createdAt = createdAt;
        this.blockID = blockID;
    }

    public UtilityHistoryView(int utilityTypeId, String utilityName, BigDecimal oldPrice, BigDecimal newPrice, String changedBy, Date createdAt, boolean isUtility, int blockID) {
        this.utilityTypeId = utilityTypeId;
        this.utilityName = utilityName;
        this.oldPrice = oldPrice;
        this.newPrice = newPrice;
        this.changedBy = changedBy;
        this.createdAt = createdAt;
        this.isUtility = isUtility;
        this.blockID = blockID;
    }

    public UtilityHistoryView(int utilityTypeId, String utilityName, BigDecimal oldPrice, BigDecimal newPrice, String changedBy, Date createdAt) {
        this.utilityTypeId = utilityTypeId;
        this.utilityName = utilityName;
        this.oldPrice = oldPrice;
        this.newPrice = newPrice;
        this.changedBy = changedBy;
        this.createdAt = createdAt;
    }

    // Constructor dùng cho lịch sử giá sắp áp dụng
    public UtilityHistoryView(int utilityTypeId, String utilityName, BigDecimal currentPrice, BigDecimal upcomingPrice, Date changeAt, Date applyAt, boolean isScheduled) {
        this.utilityTypeId = utilityTypeId;
        this.utilityName = utilityName;
        this.oldPrice = currentPrice;
        this.newPrice = upcomingPrice;
        this.createdAt = changeAt;
        this.applyAt = applyAt;
        this.isScheduled = isScheduled;
    }

    public UtilityHistoryView(int aInt, String string, BigDecimal bigDecimal, BigDecimal bigDecimal0, Object object, Date date, Date date0) {
    }

    public int getUtilityTypeId() {
        return utilityTypeId;
    }

    public void setUtilityTypeId(int utilityTypeId) {
        this.utilityTypeId = utilityTypeId;
    }

    public String getUtilityName() {
        return utilityName;
    }

    public void setUtilityName(String utilityName) {
        this.utilityName = utilityName;
    }

    public BigDecimal getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(BigDecimal oldPrice) {
        this.oldPrice = oldPrice;
    }

    public BigDecimal getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(BigDecimal newPrice) {
        this.newPrice = newPrice;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getApplyAt() {
        return applyAt;
    }

    public void setApplyAt(Date applyAt) {
        this.applyAt = applyAt;
    }

    public boolean isScheduled() {
        return isScheduled;
    }

    public void setScheduled(boolean isScheduled) {
        this.isScheduled = isScheduled;
    }

    public boolean isIsUtility() {
        return isUtility;
    }

    public void setIsUtility(boolean isUtility) {
        this.isUtility = isUtility;
    }

    public int getBlockID() {
        return blockID;
    }

    public void setBlockID(int blockID) {
        this.blockID = blockID;
    }
}
