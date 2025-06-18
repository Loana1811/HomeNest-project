/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Date;

/**
 *
 * @author ThanhTruc
 */
public class Tenant {
    private int tenantID;
    private int customerID;
    private Date joinDate;

    // Constructors
    public Tenant() {
    }

    public Tenant(int tenantID, int customerID, Date joinDate) {
        this.tenantID = tenantID;
        this.customerID = customerID;
        this.joinDate = joinDate;
    }

    // Getter & Setter
    public int getTenantID() {
        return tenantID;
    }

    public void setTenantID(int tenantID) {
        this.tenantID = tenantID;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public Date getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }
    
}
