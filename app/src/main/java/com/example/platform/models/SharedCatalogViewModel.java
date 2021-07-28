package com.example.platform.models;

import androidx.lifecycle.ViewModel;

public class SharedCatalogViewModel extends ViewModel {

    public static final String TAG = "SharedCatalogViewModel";
    public String value;
    public String genreName;
    public int genreId;

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreId(int genreId) {
        this.genreId = genreId;
    }

    public int getGenreId() {
        return genreId;
    }
}
