package Model;

import java.math.BigDecimal;

public class IncurredFee {

    private int incurredFeeID;
    private int billID;                 // FK → Bills
    private int incurredFeeTypeID;      // FK → IncurredFeeTypes
    private BigDecimal amount;

    public IncurredFee() {}

    public IncurredFee(int incurredFeeID, int billID, int incurredFeeTypeID, BigDecimal amount) {
        this.incurredFeeID = incurredFeeID;
        this.billID = billID;
        this.incurredFeeTypeID = incurredFeeTypeID;
        this.amount = amount;
    }

    public int getIncurredFeeID() {
        return incurredFeeID;
    }

    public void setIncurredFeeID(int incurredFeeID) {
        this.incurredFeeID = incurredFeeID;
    }

    public int getBillID() {
        return billID;
    }

    public void setBillID(int billID) {
        this.billID = billID;
    }

    public int getIncurredFeeTypeID() {
        return incurredFeeTypeID;
    }

    public void setIncurredFeeTypeID(int incurredFeeTypeID) {
        this.incurredFeeTypeID = incurredFeeTypeID;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

   
}
