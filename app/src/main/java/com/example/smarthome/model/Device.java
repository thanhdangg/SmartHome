package com.example.smarthome.model;

public class Device {
    private int id;
    private String iconPath;
    private String name;
    private String type;
    private boolean status;
    private String room;

    public Device() {
    }

    public Device(int id, String iconPath, String name, String type, boolean status, String room) {
        this.id = id;
        this.iconPath = iconPath;
        this.name = name;
        this.type = type;
        this.status = status;
        this.room = room;
    }

    public Device(String iconPath, String name, String type, boolean status, String room) {
        this.iconPath = iconPath;
        this.name = name;
        this.type = type;
        this.status = status;
        this.room = room;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isStatus() {
        return status;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }
}
