package com.example.micke.myapplication;

public class PointOfInterest {
    public String category;
    public String titel;
    public String description;
    public float latitude;
    public float longitude;
    public int floor;
    private String id;

    public PointOfInterest() {

    }

    public PointOfInterest(String c, String t, String d, float lat, float lon, int f, String id){
        category = c;
        if(t == "")
            titel = c;
        else
            titel = t;
        description = d;
        latitude = lat;
        longitude = lon;
        floor = f;
        this.id = id;
    }

    public PointOfInterest(PointOfInterest p) {
        category = p.getCategory();
        titel = p.getTitel();
        description = p.getDescription();
        latitude = p.getLatitude();
        longitude = p.getLongitude();
        floor = p.getFloor();
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

    public float getLatitude() {
        return latitude;
    }

    public String getTitel() {
        return titel;
    }

    public float getLongitude() {
        return longitude;
    }

    public int getFloor() {
        return floor;
    }
}
