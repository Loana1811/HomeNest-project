/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.math.BigDecimal;
import java.sql.Date;

/**
 *
 * @author kloane
 */
public class UtilityHistoryView {
    private int utilityTypeId;
    private String utilityName;
    private BigDecimal oldPrice;
    private BigDecimal newPrice;
    private String changedBy;
    private Date createdAt;
    private boolean isUtility;

    public UtilityHistoryView(int utilityTypeId, String utilityName, BigDecimal oldPrice, BigDecimal newPrice, String changedBy, Date createdAt) {
    this.utilityTypeId = utilityTypeId;
    this.utilityName = utilityName;
    this.oldPrice = oldPrice;
    this.newPrice = newPrice;
    this.changedBy = changedBy;
    this.createdAt = createdAt; // 
}
    
    public UtilityHistoryView(int utilityTypeId, String utilityName, BigDecimal oldPrice, BigDecimal newPrice, String changedBy, Date createdAt, boolean isUtility) {
        this.utilityTypeId = utilityTypeId;
        this.utilityName = utilityName;
        this.oldPrice = oldPrice;
        this.newPrice = newPrice;
        this.changedBy = changedBy;
        this.createdAt = createdAt;
        this.isUtility = isUtility;
    }

    public boolean isIsUtility() {
        return isUtility;
    }

    public void setIsUtility(boolean isUtility) {
        this.isUtility = isUtility;
    }
    
    


    public int getUtilityTypeId() {
        return utilityTypeId;
    }

    public String getUtilityName() {
        return utilityName;
    }

    public BigDecimal getOldPrice() {
        return oldPrice;
    }

    public BigDecimal getNewPrice() {
        return newPrice;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

   

    public void setUtilityTypeId(int utilityTypeId) {
        this.utilityTypeId = utilityTypeId;
    }

    public void setUtilityName(String utilityName) {
        this.utilityName = utilityName;
    }

    public void setOldPrice(BigDecimal oldPrice) {
        this.oldPrice = oldPrice;
    }

    public void setNewPrice(BigDecimal newPrice) {
        this.newPrice = newPrice;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

   
    
}


