package com.example.platform.models;

import androidx.lifecycle.ViewModel;

public class SharedCatalogViewModel extends ViewModel {

    public static final String TAG = "SharedCatalogViewModel";
    public String value;

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
