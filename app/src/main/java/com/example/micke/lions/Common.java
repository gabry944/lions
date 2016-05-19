package com.example.micke.lions;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MenuItem;


/** A file for common variables and functions for different activities **/

public class Common {


    static String TAG = "Common";

    public static final int ASK_FOR_PERMISSION = 0;
    public static final int PERMISSION_GRANTED = 1;
    public static final int PERMISSION_DENIED = 2;

    private static boolean admin = false;
    private static int location_permission = ASK_FOR_PERMISSION;
    private static int read_storage_permission = ASK_FOR_PERMISSION;

    public static boolean IsAdmin(){
        return admin;
    }

    /** returns true if user is now admin, otherwise false **/
    public static boolean MakeAdmin(InloggChange map, InloggChange list, InloggChange qr){
        admin = true;
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
        Log.d(TAG, "setting adminbutton");
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

    public static int IsLocationPermitted(Context context) {
        if (ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
        {
            location_permission = PERMISSION_GRANTED;
        }

        return location_permission;
    }

    public static void LocationPermissionDenied(){
        location_permission = PERMISSION_DENIED;
    }

    public static boolean IsReadMediaPermitted(Context context) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED)
        {
            read_storage_permission = PERMISSION_GRANTED;
        }

        return read_storage_permission == PERMISSION_GRANTED;
    }
}
