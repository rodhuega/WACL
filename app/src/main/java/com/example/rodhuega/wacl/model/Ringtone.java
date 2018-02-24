package com.example.rodhuega.wacl.model;

import java.io.Serializable;

/**
 * Created by pillo on 24/02/2018.
 */

public class Ringtone implements Serializable {
    private String name, uri;
    private int id;

    public Ringtone(String name, String uri, int id) {
        this.id=id;
        this.name=name;
        this.uri=uri;
    }

    //Gets

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUri() {
        return uri;
    }

    //Sets

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
