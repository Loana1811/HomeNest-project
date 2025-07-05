/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author kloane
 */

public class RoomStatusDTO {
    private int roomId;
    private String roomName;
    private boolean fullyRecorded;
    private boolean closed;

    public RoomStatusDTO() {}

    public RoomStatusDTO(int roomId, String roomName, boolean fullyRecorded, boolean closed) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.fullyRecorded = fullyRecorded;
        this.closed = closed;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public boolean isFullyRecorded() {
        return fullyRecorded;
    }

    public void setFullyRecorded(boolean fullyRecorded) {
        this.fullyRecorded = fullyRecorded;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }
}
