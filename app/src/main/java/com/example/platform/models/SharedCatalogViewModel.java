package com.example.platform.models;

import androidx.lifecycle.ViewModel;

public class SharedCatalogViewModel extends ViewModel {

    public static final String TAG = "SharedCatalogViewModel";
    public String mediaType;

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getMediaType() {
        return mediaType;
    }
}
