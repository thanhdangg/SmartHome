package com.example.smarthome.ui.login;

public class LoggedInUserView {
    private String displayName;

    public LoggedInUserView(String displayName) {
        this.displayName = displayName;
    }

    String getDisplayName() {
        return displayName;
    }
}