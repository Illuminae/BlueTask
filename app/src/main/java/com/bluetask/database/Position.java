package com.bluetask.database;

import java.io.Serializable;

/**
 * Created by Erik on 29/03/2016.
 * Each instance of the position class will hold information on where goods specified in a reminder
 * can be purchased.
 */
public class Position implements Serializable{

    private int id;
    private String title;
    private int radius;
    private String geo_data;

    public Position(int id, String title, int radius, String geo_data) {
        this.id = id;
        this.title = title;
        this.radius = radius;
        this.geo_data = geo_data;
    }

    public Position(String title, int radius, String geo_data) {
        this.title = title;
        this.radius = radius;
        this.geo_data = geo_data;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public String getGeo_data() {
        return geo_data;
    }

    public void setGeo_data(String geo_data) {
        this.geo_data = geo_data;
    }


}
