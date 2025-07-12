package model;
import model.Role;
import model.Block;
import java.util.Date;


public class User {

    private int userId;
    private String userFullName;
    private String email;
    private String phoneNumber;
    private String password;
    private int roleId;
    private String roleName;
    private String userStatus;
    private Date userCreatedAt;

    private Integer blockId;
    private String googleId;
    private String avatarUrl;
    private boolean isVerified;
 

    // Constructors
    public User() {
    }

    public User(int userId, String userFullName, String email, String phoneNumber, String password,
                int roleId, String userStatus, Date userCreatedAt, Integer blockId) {
        this.userId = userId;
        this.userFullName = userFullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.roleId = roleId;
        this.userStatus = userStatus;
        this.userCreatedAt = userCreatedAt;
        this.blockId = blockId;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
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


    public Integer getBlockId() {
        return blockId;
    }

    public void setBlockId(Integer blockId) {
        this.blockId = blockId;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean isVerified) {
        this.isVerified = isVerified;
    }
        private Role role;
    private Block block;

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

}
