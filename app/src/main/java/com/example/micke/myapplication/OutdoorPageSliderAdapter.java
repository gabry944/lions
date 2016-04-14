package com.example.micke.myapplication;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class OutdoorPageSliderAdapter extends FragmentPagerAdapter {
    public Context mContext;

    public OutdoorPageSliderAdapter(FragmentManager fm, Context con) {
        super(fm);
        mContext = con;
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0) {
            ((OutdoorActivity)mContext).map = OutdoorMapFragment.newInstance(position + 1);
            return ((OutdoorActivity)mContext).map;
        } else if(position == 1) {
            ((OutdoorActivity)mContext).list = OutdoorListFragment.newInstance(position + 1);
            return ((OutdoorActivity)mContext).list;
        } else {
            ((OutdoorActivity)mContext).qr = QRFragment.newInstance(position + 1);
            return ((OutdoorActivity)mContext).qr;
        }
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "SECTION 1";
            case 1:
                return "SECTION 2";
            case 2:
                return "SECTION 3";
        }
        return null;
    }
}
