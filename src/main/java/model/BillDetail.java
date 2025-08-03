package model;

import java.sql.Date;

public class BillDetail {
    private int billID;
    private int contractID;
    private Date billDate;
    private String billStatus;
    private boolean isSent;
    private Integer replacedBillID;

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
        return billDate;
    }

    public void setBillDate(Date billDate) {
        this.billDate = billDate;
    }

    public String getBillStatus() {
        return billStatus;
    }

    public void setBillStatus(String billStatus) {
        this.billStatus = billStatus;
    }

    public boolean getIsSent() {
        return isSent;
    }

    public void setIsSent(boolean isSent) {
        this.isSent = isSent;
    }

    public Integer getReplacedBillID() {
        return replacedBillID;
    }

    public void setReplacedBillID(Integer replacedBillID) {
        this.replacedBillID = replacedBillID;
    }
}
