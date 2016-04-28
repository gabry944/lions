package com.example.micke.lions.outdoor;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.micke.lions.Common;
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

        final EditText title = (EditText) dialogView.findViewById(R.id.add_building_title);
        final TextView titleText = (TextView) dialogView.findViewById(R.id.add_building_title_text);

        if(Common.IsAdmin()) {
            title.setVisibility(View.VISIBLE);
            titleText.setText(R.string.add_building_title);
            dialogBuilder
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Send the positive button event back to the host activity
//                            mListener.onDialogPositiveClick(NoticeDialogFragment.this);
                            //Save to database
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
        }
        else {
            title.setVisibility(View.GONE);
            titleText.setText("För att lägga till en byggnad måste du vara admin");
            dialogBuilder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // Cancel without anything happening
                }
            });
        }
        // Create the AlertDialog object and return it
        return dialogBuilder.create();
    }
}
