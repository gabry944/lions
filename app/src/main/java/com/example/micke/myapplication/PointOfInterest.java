package com.example.micke.myapplication;

public class PointOfInterest {
    public String category;
    public String titel;
    public String description;
    public float latitude;
    public float longitude;
    public int floor;
    private int ID;

    public PointOfInterest(){
        ID = 0;
    }

    public PointOfInterest(String c, String t, String d, float lat, float lon, int f){
        category = c;
        if(t == "")
            titel = c;
        else
            titel = t;
        description = d;
        latitude = lat;
        longitude = lon;
        floor = f;
        ID = getNewID();
    }

    public int getID() {
        return ID;
    }

    private int getNewID() {
        //get ID from server
        return 0;
    }
}
