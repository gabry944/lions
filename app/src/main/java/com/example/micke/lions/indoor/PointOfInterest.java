package com.example.micke.lions.indoor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class PointOfInterest {
    private String category;
    private String title;
    private String description;
    private float latitude;
    private float longitude;
    private String floor;
    private String id;
    private boolean official;

    public PointOfInterest() {
        id = "0";
    }

    public PointOfInterest(String title, String description, String category, float latitude, float longitude, String floor, boolean official, String id){
        this.category = category;
        if(title == "")
            this.title = category;
        else
            this.title = title;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.floor = floor;
        this.id = id;
        this.official = official;
    }

    public PointOfInterest(PointOfInterest p) {
        category = p.getCategory();
        title = p.getTitle();
        floor = p.getFloor();
        description = p.getDescription();
        latitude = p.getLatitude();
        longitude = p.getLongitude();
        floor = p.getFloor();
        id = p.getId();
        official = p.getOfficial();
    }

    public String getId() {
        return id;
    }

    public boolean getOfficial() { return official; }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public float getLatitude() { return latitude; }

    public String getTitle() { return title; }

    public float getLongitude() { return longitude; }

    public String getFloor() { return floor; }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }
}
