/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ThanhTruc
 */
public class Bill {

    private int billID;
    private int contractID;
    private Date BillDate;
    private BigDecimal totalAmount;
    private String billStatus;
    private String roomNumber;
    private BigDecimal roomRent;
    private List<UtilityReading> utilityReadings = new ArrayList<>();

    public Bill(int billID, int contractID, Date BillDate, BigDecimal totalAmount, String billStatus) {
        this.billID = billID;
        this.contractID = contractID;
        this.BillDate = BillDate;
        this.totalAmount = totalAmount;
        this.billStatus = billStatus;
    }

    public Bill() {
    }

    public int getBillID() {
        return billID;
    }

    public void setBillID(int billID) {
        this.billID = billID;
    }

    public int getContractID() {
        return contractID;
    }

    public void setContractID(int contractID) {
        this.contractID = contractID;
    }

    public Date getBillDate() {
        return BillDate;
    }

    public void setBillDate(Date BillDate) {
        this.BillDate = BillDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getBillStatus() {
        return billStatus;
    }

    public void setBillStatus(String billStatus) {
        this.billStatus = billStatus;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public BigDecimal getRoomRent() {
        return roomRent;
    }

    public void setRoomRent(BigDecimal roomRent) {
        this.roomRent = roomRent;
    }

    public List<UtilityReading> getUtilityReadings() {
        return utilityReadings;
    }

    public void setUtilityReadings(List<UtilityReading> utilityReadings) {
        this.utilityReadings = utilityReadings;
    }
    @Override
public String toString() {
    return "Bill{" +
            "billID=" + billID +
            ", roomNumber='" + roomNumber + '\'' +
            ", roomRent=" + roomRent +
            ", totalAmount=" + totalAmount +
            ", billStatus='" + billStatus + '\'' +
            ", billDate=" + BillDate +
            '}';
}

}
