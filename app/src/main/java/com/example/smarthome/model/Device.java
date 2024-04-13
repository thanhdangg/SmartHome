package com.example.smarthome.model;

public class Device {
    private String iconPath;
    private String name;
    private String type;
    private boolean status;
    private String room;

    public Device(String iconPath,String name, String type, boolean status, String room) {
        this.iconPath = iconPath;
        this.name = name;
        this.type = type;
        this.status = status;
        this.room = room;
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
