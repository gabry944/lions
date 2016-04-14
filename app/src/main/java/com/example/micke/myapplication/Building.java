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

    public Building() {

    }

    public Building(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public Building(Building building) {
        this.name = building.getName();
        this.id = building.getId();
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}
