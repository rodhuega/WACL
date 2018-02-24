package com.example.rodhuega.wacl.model;

import java.io.Serializable;

/**
 * Created by pillo on 24/02/2018.
 */

public class LocationPS implements Serializable{
    private double latitude, longitude;
    private String address;

    public LocationPS(double latitude, double longitude, String address) {
        this.latitude=latitude;
        this.longitude = longitude;
        this.address=address;
    }

    //Gets


    public String getAddress() {
        return address;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    //Sets

    public void setAddress(String name) {
        this.address = name;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
