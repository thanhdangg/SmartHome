package com.example.smarthome.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherResponse {

    @SerializedName("main")
    private Main main;

    @SerializedName("clouds")
    private Clouds clouds;

    @SerializedName("name")
    private String name;
    @SerializedName("weather")
    private List<Weather> weather;

    public Main getMain() {
        return main;
    }

    public Clouds getClouds() {
        return clouds;
    }

    public List<Weather> getWeather() {
        return weather;
    }

    public String getName() {
        return name;
    }

    public static class Main {

        @SerializedName("temp")
        private float temp;

        @SerializedName("temp_min")
        private float tempMin;

        @SerializedName("temp_max")
        private float tempMax;

        @SerializedName("humidity")
        private int humidity;

        public float getTemp() {
            return temp;
        }

        public float getTempMin() {
            return tempMin;
        }

        public float getTempMax() {
            return tempMax;
        }

        public int getHumidity() {
            return humidity;
        }
    }

    public static class Clouds {

        @SerializedName("all")
        private int all;

        public int getAll() {
            return all;
        }
    }
    public static class Weather {
        @SerializedName("id")
        private int id;

        @SerializedName("main")
        private String main;

        @SerializedName("description")
        private String description;

        @SerializedName("icon")
        private String icon;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getMain() {
            return main;
        }

        public void setMain(String main) {
            this.main = main;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }
    }
}