package com.bluetask.database;

/**
 * Created by Erik on 29/03/2016.
 * Each instance of the position class will hold information on where goods specified in a reminder
 * can be purchased.
 */
public class Position {

    private int id;
    private String title;
    private String street;
    private String str_num;
    private int zip;
    private String city;
    private String geo_data;

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

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStr_num() {
        return str_num;
    }

    public void setStr_num(String str_num) {
        this.str_num = str_num;
    }

    public int getZip() {
        return zip;
    }

    public void setZip(int zip) {
        this.zip = zip;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getGeo_data() {
        return geo_data;
    }

    public void setGeo_data(String geo_data) {
        this.geo_data = geo_data;
    }


}
