/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.Objects;

/**
 *
 * @author ThanhTruc
 */
public class Block {

    private int blockID;
    private String blockName;
    private int maxRooms;
    private int roomCount;
    private String blockStatus;

    public Block() {
    }

    public Block(int blockID, String blockName, int maxRooms, int roomCount, String blockStatus) {
        this.blockID = blockID;
        this.blockName = blockName;
        this.maxRooms = maxRooms;
        this.roomCount = roomCount;
        this.blockStatus = blockStatus;
    }

    public Block(int aInt, String string) {
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

    public int getMaxRooms() {
        return maxRooms;
    }

    public void setMaxRooms(int maxRooms) {
        this.maxRooms = maxRooms;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Block block = (Block) o;
        return blockID == block.blockID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(blockID);
    }

}
