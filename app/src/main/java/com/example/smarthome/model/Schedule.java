package com.example.smarthome.model;

public class Schedule {
    private String deviceName;
    private String timeOn;
    private String timeOff;
    private String deviceRoom;

    public Schedule(String deviceName, String timeOn, String timeOff, String deviceRoom) {
        this.deviceName = deviceName;
        this.timeOn = timeOn;
        this.timeOff = timeOff;
        this.deviceRoom = deviceRoom;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getTimeOn() {
        return timeOn;
    }

    public void setTimeOn(String timeOn) {
        this.timeOn = timeOn;
    }

    public String getTimeOff() {
        return timeOff;
    }

    public void setTimeOff(String timeOff) {
        this.timeOff = timeOff;
    }

    public String getDeviceRoom() {
        return deviceRoom;
    }

    public void setDeviceRoom(String deviceRoom) {
        this.deviceRoom = deviceRoom;
    }
}