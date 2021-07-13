package com.example.platform;

import android.app.Application;

import com.parse.Parse;

public class ParseApplication extends Application {
    // Initializes Parse SDK as soon as the application is created
    @Override
    public void onCreate() {
        super.onCreate();

        // TODO: Register your parse models
        //ParseObject.registerSubclass(Post.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getResources().getString(R.string.parseApplicationID))
                .clientKey(getResources().getString(R.string.parseClientKey))
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
