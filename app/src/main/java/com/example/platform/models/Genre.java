package com.example.platform.models;

public class Genre {

    public static final String TAG = "Genre";
    public String name;
    public int id;
    public String type;

    public Genre(String name, int id, String type) {
        this.name = name;
        this.id = id;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }
}
