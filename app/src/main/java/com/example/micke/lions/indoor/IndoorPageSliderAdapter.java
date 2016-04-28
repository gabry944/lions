package com.example.micke.lions.indoor;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.example.micke.lions.outdoor.OutdoorQRFragment;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class IndoorPageSliderAdapter extends FragmentPagerAdapter {
    public Context mContext;

    public IndoorPageSliderAdapter(FragmentManager fm, Context con) {
        super(fm);
        mContext = con;
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0) {
            ((IndoorActivity)mContext).map = IndoorMapFragment.newInstance(position + 1);
            return ((IndoorActivity)mContext).map;
        } else if(position == 1) {

            ((IndoorActivity)mContext).list = IndoorListFragment.newInstance(position + 1);
            return ((IndoorActivity)mContext).list;
        } else {
            ((IndoorActivity)mContext).qr = IndoorQRFragment.newInstance(position + 1);
            return ((IndoorActivity)mContext).qr;
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
