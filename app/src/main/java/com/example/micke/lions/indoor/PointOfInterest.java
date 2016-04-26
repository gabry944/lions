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

    public PointOfInterest() {

    }

    public PointOfInterest(String title, String description, String category, float latitude, float longitude, String floor, String id){
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
    }

    public PointOfInterest(PointOfInterest p) {
        category = p.getCategory();
        title = p.getTitle();
        description = p.getDescription();
        latitude = p.getLatitude();
        longitude = p.getLongitude();
        id = p.getId();
    }

    public String getId() {
        return id;
    }

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
