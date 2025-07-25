package model;

import java.sql.Date;
import java.sql.Timestamp;

public class Report {

    private int reportID;
    private int customerID;
    private int roomID;
    private String roomNumber;
    private int contractID;
    private String issueDescription;
    private String reportStatus;
    private Timestamp reportCreatedAt;
    private Integer resolvedBy;
    private Timestamp resolvedDate;
    private String customerName; // Added for customer name
    private Date startDate; // Added for contract start date
    private Date endDate; // Added for contract end date

    public Report(int reportID, int customerID, int roomID, String roomNumber, int contractID, String issueDescription, String reportStatus, Timestamp reportCreatedAt, Integer resolvedBy, Timestamp resolvedDate, String customerName, Date startDate, Date endDate) {
        this.reportID = reportID;
        this.customerID = customerID;
        this.roomID = roomID;
        this.roomNumber = roomNumber;
        this.contractID = contractID;
        this.issueDescription = issueDescription;
        this.reportStatus = reportStatus;
        this.reportCreatedAt = reportCreatedAt;
        this.resolvedBy = resolvedBy;
        this.resolvedDate = resolvedDate;
        this.customerName = customerName;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Constructor
    public Report() {
    }

    // Getters and Setters
    public int getReportID() {
        return reportID;
    }

    public void setReportID(int reportID) {
        this.reportID = reportID;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public int getRoomID() {
        return roomID;
    }

    public void setRoomID(int roomID) {
        this.roomID = roomID;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getContractID() {
        return contractID;
    }

    public void setContractID(int contractID) {
        this.contractID = contractID;
    }

    public String getIssueDescription() {
        return issueDescription;
    }

    public void setIssueDescription(String issueDescription) {
        this.issueDescription = issueDescription;
    }

    public String getReportStatus() {
        return reportStatus;
    }

    public void setReportStatus(String reportStatus) {
        this.reportStatus = reportStatus;
    }

    public Timestamp getReportCreatedAt() {
        return reportCreatedAt;
    }

    public void setReportCreatedAt(Timestamp reportCreatedAt) {
        this.reportCreatedAt = reportCreatedAt;
    }

    public Integer getResolvedBy() {
        return resolvedBy;
    }

    public void setResolvedBy(Integer resolvedBy) {
        this.resolvedBy = resolvedBy;
    }

    public Timestamp getResolvedDate() {
        return resolvedDate;
    }

    public void setResolvedDate(Timestamp resolvedDate) {
        this.resolvedDate = resolvedDate;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
