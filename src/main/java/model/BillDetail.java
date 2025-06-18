/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author ThanhTruc
 */
public class BillDetail {
    private int billDetailID;
    private int billID;
    private float roomrent;
    private float electricityCost;
    private float waterCost;
     private float wifiCost;

    public BillDetail(int billDetailID, int billID, float roomrent, float electricityCost, float waterCost, float wifiCost) {
        this.billDetailID = billDetailID;
        this.billID = billID;
        this.roomrent = roomrent;
        this.electricityCost = electricityCost;
        this.waterCost = waterCost;
        this.wifiCost = wifiCost;
    }

    public int getBillDetailID() {
        return billDetailID;
    }

    public void setBillDetailID(int billDetailID) {
        this.billDetailID = billDetailID;
    }

    public int getBillID() {
        return billID;
    }

    public void setBillID(int billID) {
        this.billID = billID;
    }

    public float getRoomrent() {
        return roomrent;
    }

    public void setRoomrent(float roomrent) {
        this.roomrent = roomrent;
    }

    public float getElectricityCost() {
        return electricityCost;
    }

    public void setElectricityCost(float electricityCost) {
        this.electricityCost = electricityCost;
    }

    public float getWaterCost() {
        return waterCost;
    }

    public void setWaterCost(float waterCost) {
        this.waterCost = waterCost;
    }

    public float getWifiCost() {
        return wifiCost;
    }

    public void setWifiCost(float wifiCost) {
        this.wifiCost = wifiCost;
    }
     
}
