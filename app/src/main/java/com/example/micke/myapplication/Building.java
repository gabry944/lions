package com.example.micke.myapplication;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by iSirux on 2016-04-12.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Building {

    private String name;
    private String id;
    private double latitude;
    private double longitude;

    public Building() {

    }

    public Building(String name, String id, double latitude, double longitude) {
        this.name = name;
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Building(Building building) {
        this.name = building.getName();
        this.id = building.getId();
        this.latitude = building.latitude;
        this.longitude = building.longitude;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }
}
