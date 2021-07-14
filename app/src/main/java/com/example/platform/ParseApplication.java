package com.example.platform;

import android.app.Application;

import com.example.platform.models.Title;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {
    // Initializes Parse SDK as soon as the application is created
    @Override
    public void onCreate() {
        super.onCreate();

        // Register Parse models
        ParseObject.registerSubclass(Title.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getResources().getString(R.string.parseApplicationID))
                .clientKey(getResources().getString(R.string.parseClientKey))
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}