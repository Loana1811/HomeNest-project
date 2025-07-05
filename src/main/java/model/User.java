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
public class User {

    private int userID;
    private String userFullName;
    private String email;
    private String phoneNumber;
    private String password;
    private Role role;
    private String userStatus;
    private Date userCreatedAt;
    private Block block;

    public User() {
    }

    public User(int userID, String userFullName, String email, String phoneNumber,
            String password, Role role, String userStatus, Date userCreatedAt, Block block) {
        this.userID = userID;
        this.userFullName = userFullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.role = role;
        this.userStatus = userStatus;
        this.userCreatedAt = userCreatedAt;
        this.block = block;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public Date getUserCreatedAt() {
        return userCreatedAt;
    }

    public void setUserCreatedAt(Date userCreatedAt) {
        this.userCreatedAt = userCreatedAt;
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public Integer getBlockID() {
        return (block != null) ? block.getBlockID() : null;
    }

    public void setBlockID(Integer blockID) {
        if (blockID == null) {
            this.block = null;
        } else {
            if (this.block == null) {
                this.block = new Block();
            }
            this.block.setBlockID(blockID);
        }
    }

    public void setRoleID(int roleID) {
        if (this.role == null) {
            this.role = new Role();
        }
        this.role.setRoleID(roleID);
    }

    public int getRoleID() {
        return role != null ? role.getRoleID() : 0;
    }
}
