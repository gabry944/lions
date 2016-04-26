package com.example.micke.lions;

import android.util.Log;

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
}
