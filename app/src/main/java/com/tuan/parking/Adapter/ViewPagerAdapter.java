package com.tuan.parking.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.tuan.parking.FragmentTabs.ListFragment;
import com.tuan.parking.FragmentTabs.MapFragment;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by hp1 on 21-01-2015.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created
    Map<Integer, Fragment>  mPagesReferenceMap = new HashMap<>();

    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapter(FragmentManager fm,CharSequence mTitles[], int mNumbOfTabsumb) {
        super(fm);
        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;

    }

    public MapFragment getMapFragment(int key) {
        return (MapFragment) mPagesReferenceMap.get(key);
    }

    public ListFragment getListFragment(int key) {
        return (ListFragment) mPagesReferenceMap.get(key);
    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {

        if(position == 0) // if the position is 0 we are returning the First tab
        {
            MapFragment mapFragment = MapFragment.newInstance();
            double lat = 41.8857256;
            double lng = -87.6369590;
            mapFragment.setLat(lat);
            mapFragment.setLng(lng);


            mPagesReferenceMap.put(position, mapFragment);

            return mapFragment;
        }
        else             // As we are having 2 tabs if the position is now 0 it must be 1 so we are returning second tab
        {
            ListFragment tab2 = ListFragment.newInstance();
            tab2.onResume();
            mPagesReferenceMap.put(position, tab2);
            
            return tab2;
        }


    }

    // This method return the titles for the Tabs in the Tab Strip

    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    // This method return the Number of tabs for the tabs Strip

    @Override
    public int getCount() {
        return NumbOfTabs;
    }
}
