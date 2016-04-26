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

    private static boolean admin = false;

    public static boolean IsAdmin(){
        return admin;
    }

    /** returns true if user is now admin, otherwise false **/
    public static boolean MakeAdmin(){
        admin = true;
        Log.d("Common", "MakeAdmin: Admin = " + admin);
        return admin;
    }

    public static void LogOut(){
        admin = false;
        Log.d("Common", "LogOut: Admin = " + admin);
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
