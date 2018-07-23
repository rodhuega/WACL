package com.example.rodhuega.wacl.model.WeatherJSON;

public class Wind {
    private double speed,gust,deg;

    public double getDeg() {
        return deg;
    }

    public double getGust() {
        return gust;
    }

    public double getSpeed() {
        return speed;
    }

    public void setDeg(double deg) {
        this.deg = deg;
    }

    public void setGust(double gust) {
        this.gust = gust;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}
