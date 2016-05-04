package com.bluetask;

/**
 * Created by frede on 5/1/2016.
 */
public class LocationPair {
    private String locDesc;
    private String locCoords;

    public LocationPair(String locDesc,String locCoords){
        this.locDesc=locDesc;
        this.locCoords=locCoords;
    }

    public String getLocDesc(){
        return locDesc;
    }

    public String getLocCoords(){
        return locCoords;
    }

    //accessors
}
