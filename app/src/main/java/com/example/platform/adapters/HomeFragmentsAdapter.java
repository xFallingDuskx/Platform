package com.example.platform.adapters;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import java.util.ArrayList;
import java.util.List;

public class HomeFragmentsAdapter extends FragmentStatePagerAdapter {

    public static final String TAG = "HomeFragmentsAdapter";

    private List<String> tabTitles = new ArrayList<>();
    private List<Fragment> tabFragments = new ArrayList<>();

    public HomeFragmentsAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return tabFragments.get(position);
    }

    @Override
    public int getCount() {
        return tabFragments.size();
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles.get(position);
    }

    public void addFragment(Fragment fragment, String title) {
        tabTitles.add(title);
        tabFragments.add(fragment);
    }
}
