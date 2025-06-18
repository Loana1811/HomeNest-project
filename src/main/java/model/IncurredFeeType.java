package model;

import java.math.BigDecimal;

public class IncurredFeeType {

    private int incurredFeeTypeID;
    private String feeName;
    private BigDecimal defaultAmount;

    public IncurredFeeType() {}

    public IncurredFeeType(int incurredFeeTypeID, String feeName, BigDecimal defaultAmount) {
        this.incurredFeeTypeID = incurredFeeTypeID;
        this.feeName = feeName;
        this.defaultAmount = defaultAmount;
    }

    public int getIncurredFeeTypeID() {
        return incurredFeeTypeID;
    }

    public void setIncurredFeeTypeID(int incurredFeeTypeID) {
        this.incurredFeeTypeID = incurredFeeTypeID;
    }

    public String getFeeName() {
        return feeName;
    }

    public void setFeeName(String feeName) {
        this.feeName = feeName;
    }

    public BigDecimal getDefaultAmount() {
        return defaultAmount;
    }

    public void setDefaultAmount(BigDecimal defaultAmount) {
        this.defaultAmount = defaultAmount;
    }

}
