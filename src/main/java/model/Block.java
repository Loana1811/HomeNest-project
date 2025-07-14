/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author ThanhTruc
 */
public class Block {

    private int blockID;
    private String blockName;
    private int maxRoom;
    private int roomCount;
    private String blockStatus;

    public Block(int blockID, String blockName, int maxRoom, int roomCount, String blockStatus) {
        this.blockID = blockID;
        this.blockName = blockName;
        this.maxRoom = maxRoom;
        this.roomCount = roomCount;
        this.blockStatus = blockStatus;
    }

   
    public Block() {
    }

    

    public int getBlockID() {
        return blockID;
    }

    public void setBlockID(int blockID) {
        this.blockID = blockID;
    }

    public String getBlockName() {
        return blockName;
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }

    public int getMaxRoom() {
        return maxRoom;
    }

    public void setMaxRoom(int maxRoom) {
        this.maxRoom = maxRoom;
    }

    public int getRoomCount() {
        return roomCount;
    }

    public void setRoomCount(int roomCount) {
        this.roomCount = roomCount;
    }

    public String getBlockStatus() {
        return blockStatus;
    }

    public void setBlockStatus(String blockStatus) {
        this.blockStatus = blockStatus;
    }

}
