package com.example.micke.lions;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v7.view.menu.MenuView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.micke.lions.outdoor.OutdoorMapFragment;

/** A file for common variables and functions for different activities **/

public class Common {

    static String TAG = "Common";
    private static boolean admin = false;

    public static boolean IsAdmin(){
        return admin;
    }

    /** returns true if user is now admin, otherwise false **/
    public static boolean MakeAdmin(InloggChange map, InloggChange list, InloggChange qr){
        admin = true;
        Log.d(TAG, "MakeAdmin: Admin = " + admin);
        if(map != null)
            map.adminInlogg();
        else
            Log.d(TAG, "MakeAdmin: map is null reference, restart the app ");
        if(list != null)
            list.adminInlogg();
        else
            Log.d(TAG, "MakeAdmin: list is null reference, restart the app");
        if(list != null)
            qr.adminInlogg();
        else
            Log.d(TAG, "MakeAdmin: qr is null reference, restart the app ");

        return admin;
    }

    public static void LogOut(InloggChange map, InloggChange list, InloggChange qr){
        admin = false;
        Log.d(TAG, "LogOut: Admin = " + admin);
        if(map != null)
            map.commonInlogg();
        else
            Log.d(TAG, "MakeAdmin: map is null reference, restart the app");
        if(list != null)
            list.commonInlogg();
        else
            Log.d(TAG, "MakeAdmin: list is null reference, restart the app");
        if(list != null)
            qr.commonInlogg();
        else
            Log.d(TAG, "MakeAdmin: qr is null reference, restart the app");
    }

    public static void setAdminButton(MenuItem adminButton, Context context) {
        if(admin) {
            if(Build.VERSION.SDK_INT >= 21)
                adminButton.setIcon(context.getResources().getDrawable(R.drawable.admin_button_on, null));
            else
                adminButton.setIcon(context.getResources().getDrawable(R.drawable.admin_button_on));
        }
        else {
            if(Build.VERSION.SDK_INT >= 21)
                adminButton.setIcon(context.getResources().getDrawable(R.drawable.admin_button_off, null));
            else
                adminButton.setIcon(context.getResources().getDrawable(R.drawable.admin_button_off));
        }
    }
}
