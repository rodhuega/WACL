package com.example.rodhuega.wacl.model.WeatherJSON;

public class Main {
    private double temp,pressure,temp_min,temp_max;
    private int humidity;

    public double getPressure() {
        return pressure;
    }

    public double getTemp() {
        return temp;
    }

    public double getTemp_max() {
        return temp_max;
    }

    public double getTemp_min() {
        return temp_min;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public void setTemp_max(double temp_max) {
        this.temp_max = temp_max;
    }

    public void setTemp_min(double temp_min) {
        this.temp_min = temp_min;
    }
}
