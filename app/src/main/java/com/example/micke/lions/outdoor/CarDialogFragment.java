package com.example.micke.lions.outdoor;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.micke.lions.Common;
import com.example.micke.lions.R;

public class CarDialogFragment extends DialogFragment {

    FireBaseOutdoor fireBaseHandler;
    Car car;
    public boolean dismissed;
    private boolean canUseLocation;
    private boolean gpsIsOn;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dismissed = false;
        canUseLocation = false;
        gpsIsOn = false;

        if (Common.IsLocationPermitted(getActivity()) == Common.PERMISSION_GRANTED) {
            canUseLocation = true;
        }

        Log.d("TAG", "Can use location" + String.valueOf(canUseLocation));

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

        //Get arguments
        Bundle bundle = this.getArguments();
        car = (Car) bundle.getSerializable("car");

        fireBaseHandler = ((OutdoorActivity) getActivity()).getFireBaseHandler();

        //Get GPS pos
        final LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        final String locationProvider = LocationManager.NETWORK_PROVIDER;
        // Or use LocationManager.GPS_PROVIDER

        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            gpsIsOn = true;
        }

        //Inflate the layout from xml
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.car_dialog_fragment, null);
        dialogBuilder.setView(dialogView);

        Button findCar = (Button) dialogView.findViewById(R.id.find_car);
        Button parkCar = (Button) dialogView.findViewById(R.id.park_car);
        TextView permissionMessage = (TextView) dialogView.findViewById(R.id.permission);

        if(car.getLongitude() == 0 && car.getLatitude() == 0)
            findCar.setEnabled(false);
        else {
            findCar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ActivityCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) { return; }

                    Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);

                    ((OutdoorActivity) getActivity()).getViewPager().setCurrentItem(0);
                    ((OutdoorActivity) getActivity()).map.newMarker(car, lastKnownLocation);
                    dismiss();
                }
            });
        }

        if(!canUseLocation){
            parkCar.setEnabled(false);
            permissionMessage.setVisibility(View.VISIBLE);
        }
        if(!gpsIsOn){
            parkCar.setEnabled(false);
            alertNoGps();
        }
        else {
            parkCar.setEnabled(true);
            permissionMessage.setVisibility(View.GONE);
            parkCar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ActivityCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }

                        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);

                        Log.d("TAG", String.valueOf(lastKnownLocation));
                        car.setLatitude(lastKnownLocation.getLatitude());
                        car.setLongitude(lastKnownLocation.getLongitude());
                        Log.d("car", "current pos: " + lastKnownLocation.getLatitude() + " " + lastKnownLocation.getLongitude());
                        fireBaseHandler.updateCar(car);

                        ((OutdoorActivity) getActivity()).getViewPager().setCurrentItem(0);
                        ((OutdoorActivity) getActivity()).map.newMarker(car, lastKnownLocation);

                        dismiss();
                    }

            });
        }


        // Create the AlertDialog object and return it
        return dialogBuilder.create();
    }

    private void alertNoGps(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        Log.d("TAG", String.valueOf(getActivity()));

        builder.setMessage("Din GPS verkar vara avstängd, vill du sätta på den?")
                .setCancelable(false)
                .setPositiveButton("Ja", new DialogInterface.OnClickListener(){
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("Nej", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        dismissed = true;
    }
}
