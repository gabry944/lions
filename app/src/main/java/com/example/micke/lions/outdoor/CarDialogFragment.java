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
import android.widget.EditText;

import com.example.micke.lions.FireBaseHandler;
import com.example.micke.lions.R;
import com.example.micke.lions.indoor.IndoorActivity;
import com.example.micke.lions.outdoor.Building;
import com.example.micke.lions.outdoor.Car;
import com.example.micke.lions.outdoor.NewBuildingCallback;
import com.example.micke.lions.outdoor.OutdoorActivity;
import com.example.micke.lions.outdoor.OutdoorMapFragment;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by iSirux on 2016-04-11.
 */
public class CarDialogFragment extends DialogFragment {

    FireBaseOutdoor fireBaseHandler;
    Car car;
    public boolean dismissed;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dismissed = false;
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
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }

        //Inflate the layout from xml
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.car_dialog_fragment, null);
        dialogBuilder.setView(dialogView);

        Button findCar = (Button) dialogView.findViewById(R.id.find_car);
        Button parkCar = (Button) dialogView.findViewById(R.id.park_car);

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

        parkCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) { return; }

                Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
                car.setLatitude(lastKnownLocation.getLatitude());
                car.setLongitude(lastKnownLocation.getLongitude());
                Log.d("car", "current pos: " + lastKnownLocation.getLatitude() + " " + lastKnownLocation.getLongitude());
                fireBaseHandler.newCar(car);

                ((OutdoorActivity) getActivity()).getViewPager().setCurrentItem(0);
                ((OutdoorActivity) getActivity()).map.newMarker(car, lastKnownLocation);

                dismiss();
            }
        });

        // Create the AlertDialog object and return it
        return dialogBuilder.create();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        dismissed = true;
    }
}
