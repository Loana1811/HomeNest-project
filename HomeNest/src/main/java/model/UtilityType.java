/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author ThanhTruc
 */
public class UtilityType {
    private int utilityTypeID;
    private String utilityName;
    private float unitPrice;
    private String unit;
    private boolean isSystem;

    public UtilityType(int utilityTypeID, String utilityName, float unitPrice, String unit, boolean isSystem) {
        this.utilityTypeID = utilityTypeID;
        this.utilityName = utilityName;
        this.unitPrice = unitPrice;
        this.unit = unit;
        this.isSystem = isSystem;
    }

    public int getUtilityTypeID() {
        return utilityTypeID;
    }

    public void setUtilityTypeID(int utilityTypeID) {
        this.utilityTypeID = utilityTypeID;
    }

    public String getUtilityName() {
        return utilityName;
    }

    public void setUtilityName(String utilityName) {
        this.utilityName = utilityName;
    }

    public float getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(float unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public boolean isIsSystem() {
        return isSystem;
    }

    public void setIsSystem(boolean isSystem) {
        this.isSystem = isSystem;
    }
    
}
