package com.example.platform.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.platform.R;
import com.example.platform.adapters.HomeFragmentsAdapter;
import com.google.android.material.tabs.TabLayout;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    public static final String TAG = "HomeFragment";
    FrameLayout flHome;
    ViewPager vpHome;
    TabLayout tlHome;
    HomeFragmentsAdapter adapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpTabs();
    }

    private void setUpTabs() {
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        flHome = getView().findViewById(R.id.flHome);
        vpHome = getView().findViewById(R.id.vpHome);
        adapter = new HomeFragmentsAdapter(getChildFragmentManager());
        adapter.addFragment(new HomeFragment_TvShows(), "TV Shows");
        adapter.addFragment(new HomeFragment_Movies(), "Movies");
        vpHome.setAdapter(adapter);

        // Give the TabLayout the ViewPager
        tlHome = getView().findViewById(R.id.tlHome);
        tlHome.setupWithViewPager(vpHome);
    }
}
