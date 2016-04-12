package com.example.micke.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by iSirux on 2016-04-11.
 */
public class AddPointDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

        //Inflate the layout from xml
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.add_point_layout, null);
        dialogBuilder.setView(dialogView);

        //Get arguments - reference to firebase database
        Bundle bundle = this.getArguments();
        final FireBaseIndoor fireBaseBuilding = (FireBaseIndoor) bundle.getSerializable("firebase");

        dialogBuilder
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the positive button event back to the host activity
//                            mListener.onDialogPositiveClick(NoticeDialogFragment.this);
                        //Save to database
                        EditText title = (EditText) dialogView.findViewById(R.id.add_point_title);
                        EditText description = (EditText) dialogView.findViewById(R.id.add_point_description);
                        EditText category = (EditText) dialogView.findViewById(R.id.add_point_category);
                        String ipId = fireBaseBuilding.generateId();

                        PointOfInterest point = new PointOfInterest(title.getText().toString(),
                                description.getText().toString(), category.getText().toString(), 0, 0, ipId);
                        fireBaseBuilding.updateIp(point, 4);
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
