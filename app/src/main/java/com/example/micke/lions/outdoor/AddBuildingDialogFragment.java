package com.example.micke.lions.outdoor;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.micke.lions.R;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by iSirux on 2016-04-11.
 */
public class AddBuildingDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

        //Inflate the layout from xml
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.add_building_layout, null);
        dialogBuilder.setView(dialogView);

        //Get arguments
        Bundle bundle = this.getArguments();
        final LatLng latlng = bundle.getParcelable("latlng");
        final NewBuildingCallback buildingCallback = (OutdoorMapFragment) bundle.getSerializable("mapfragment");

        dialogBuilder
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the positive button event back to the host activity
//                            mListener.onDialogPositiveClick(NoticeDialogFragment.this);
                        //Save to database
                        EditText title = (EditText) dialogView.findViewById(R.id.add_building_title);
                        String buildingId = ((OutdoorActivity) getActivity()).getFireBaseHandler().generateId();

                        Building building = new Building(title.getText().toString(), buildingId, latlng.latitude, latlng.longitude);
                        ((OutdoorActivity) getActivity()).getFireBaseHandler().updateBuilding(building);
                        buildingCallback.newMarker(building);
                    }
                })
                .setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the negative button event back to the host activity
//                            mListener.onDialogNegativeClick(NoticeDialogFragment.this);
                    }
                });
        // Create the AlertDialog object and return it
        return dialogBuilder.create();
    }
}
