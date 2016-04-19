package com.example.micke.lions.outdoor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by iSirux on 2016-04-12.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Car implements Serializable {

    private String name;
    private String id;
    private double latitude;
    private double longitude;

    public Car() {

    }

    public Car(String name, String id, double latitude, double longitude) {
        this.name = name;
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Car(Car car) {
        this.name = car.getName();
        this.id = car.getId();
        this.latitude = car.latitude;
        this.longitude = car.longitude;
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

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
