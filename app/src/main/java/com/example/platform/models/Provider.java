package com.example.platform.models;

public class Provider {

    public static final String TAG = "Provider";
    public String name;
    public String id;
    public String type;

    public Provider(String name, String id, String type) {
        this.name = name;
        this.id = id;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }
}
