package com.example.rodhuega.wacl.model.WeatherJSON;

import java.util.ArrayList;

public class WeatherContent {
    private Coordinates coord;
    private Sys sys;
    private String base,name;
    private Main main;
    private Wind wind;
    private Clouds clouds;
    private int dt,id,cod;
    private ArrayList<WeatherElement> weather;

    public int getId() {
        return id;
    }

    public ArrayList<WeatherElement> getWeather() {
        return weather;
    }

    public Clouds getClouds() {
        return clouds;
    }

    public Coordinates getCoord() {
        return coord;
    }

    public int getCod() {
        return cod;
    }

    public int getDt() {
        return dt;
    }

    public Main getMain() {
        return main;
    }

    public String getBase() {
        return base;
    }

    public String getName() {
        return name;
    }

    public Sys getSys() {
        return sys;
    }

    public Wind getWind() {
        return wind;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public void setClouds(Clouds clouds) {
        this.clouds = clouds;
    }

    public void setCod(int cod) {
        this.cod = cod;
    }

    public void setCoord(Coordinates coord) {
        this.coord = coord;
    }

    public void setMain(Main main) {
        this.main=main;
    }

    public void setDt(int dt) {
        this.dt = dt;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSys(Sys sys) {
        this.sys = sys;
    }

    public void setWeather(ArrayList<WeatherElement> weather) {
        this.weather = weather;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }
}
