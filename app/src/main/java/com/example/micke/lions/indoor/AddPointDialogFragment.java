package com.example.micke.lions.indoor;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.micke.lions.R;

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
        //final FireBaseIndoor fireBaseBuilding = (FireBaseIndoor) bundle.getSerializable("firebase");
        final FireBaseIndoor fireBaseIndoor = ((IndoorActivity) getActivity()).getFireBaseHandler();
        final float point1 = bundle.getFloat("lat", 0);
        final float point2 = bundle.getFloat("lng", 0);

        dialogBuilder
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the positive button event back to the host activity
//                            mListener.onDialogPositiveClick(NoticeDialogFragment.this);
                        //Save to database
                        EditText title = (EditText) dialogView.findViewById(R.id.add_point_title);
                        EditText description = (EditText) dialogView.findViewById(R.id.add_point_description);
                        EditText category = (EditText) dialogView.findViewById(R.id.add_point_category);
                        String ipId = fireBaseIndoor.generateId();

                        if(!title.getText().toString().equals("") &&
                                !description.getText().toString().equals("") &&
                                !category.getText().toString().equals("")) {
                            PointOfInterest point = new PointOfInterest(title.getText().toString(),
                                    description.getText().toString(), category.getText().toString(), point1, point2, ipId);
                            fireBaseIndoor.updateIp(point, Integer.parseInt(fireBaseIndoor.getFloor()));
                        }
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
