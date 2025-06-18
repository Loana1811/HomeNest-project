/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author ThanhTruc
 */
public class Role {
    private int roleID;
    private String roleName;

    public Role(int roleID, String roleName) {
        this.roleID = roleID;
        this.roleName = roleName;
    }

    public int getRoleID() {
        return roleID;
    }

    public void setRoleID(int RoleID) {
        this.roleID = RoleID;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String RoleName) {
        this.roleName = RoleName;
    }
    
    
}
