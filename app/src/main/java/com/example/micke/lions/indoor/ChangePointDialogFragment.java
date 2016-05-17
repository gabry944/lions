package com.example.micke.lions.indoor;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.micke.lions.Common;
import com.example.micke.lions.R;
import com.google.android.gms.maps.model.Marker;

import org.w3c.dom.Text;


public class ChangePointDialogFragment extends DialogFragment {

    private String TAG = "ChangePointDialogFragment";
    private IndoorActivity indoorActivity;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //get witch point to remove
        Bundle bundle = this.getArguments();
        final String point = bundle.getString("id");

        indoorActivity = (IndoorActivity) getActivity();

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.removePointQuetion);
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.fragment_indoor_change_point, null))
                // Add action buttons
                .setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d(TAG, "onClick: ta bort");

                        //tell IndoorMapFragment to remove the ip
                        indoorActivity.map.RemovePoint(point);
                    }
                })
                .setNeutralButton(R.string.change, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d(TAG, "onClick: ändra");
                        openIpChangeDialog();
                    }
                })
                .setNegativeButton(R.string.abort, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d(TAG, "onClick: avbryt");
                    }
                });

        // Create the AlertDialog object and return it
        return builder.create();
    }

    private void openIpChangeDialog()
    {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_point_layout, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);
        builder.setCancelable(false);
        builder.setTitle("Ändra punkt");

        Bundle bundle = this.getArguments();
        //get values for the current interest point
        final String ipId = bundle.getString("id");
        final String category = bundle.getString("category");
        final String title = bundle.getString("title");
        final String description = bundle.getString("description");

        final EditText titleField = (EditText) dialogView.findViewById(R.id.add_point_title);
        titleField.setText(title, TextView.BufferType.EDITABLE);
        final EditText descriptionField = (EditText) dialogView.findViewById(R.id.add_point_description);
        descriptionField.setText(description, TextView.BufferType.EDITABLE);
        final Spinner spinner = (Spinner) dialogView.findViewById(R.id.category_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.category_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        if (!category.equals(null)) {
            int spinnerPosition = adapter.getPosition(category);
            spinner.setSelection(spinnerPosition);
        }

        final CheckBox official = (CheckBox) dialogView.findViewById(R.id.official);
        final TextView offText = (TextView) dialogView.findViewById(R.id.official_text);

        if(Common.IsAdmin()) {
            official.setVisibility(View.VISIBLE);
            offText.setVisibility(View.VISIBLE);
        }
        else {
            official.setVisibility(View.GONE);
            offText.setVisibility(View.GONE);
        }

        //Get arguments - reference to firebase database
        final FireBaseIndoor fireBaseIndoor = ((IndoorActivity) getActivity()).getFireBaseHandler();
        final float point1 = bundle.getFloat("lat", 0);
        final float point2 = bundle.getFloat("lng", 0);

        Button submit = (Button) dialogView.findViewById(R.id.submit);
        Button cancel = (Button) dialogView.findViewById(R.id.cancel);
        submit.setVisibility(View.GONE);
        cancel.setVisibility(View.GONE);

        Button getQRCode = (Button) dialogView.findViewById(R.id.button_get_qr_code);
        getQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://api.qrserver.com/v1/create-qr-code/?color=000000&bgcolor=FFFFFF&data=" +
                        "building/" + indoorActivity.getBuildingId() + "/floor/"
                        + fireBaseIndoor.getFloor() + "/ip/" + ipId
                        + "&qzone=1&margin=0&size=400x400&ecc=L";

                Log.d("url", url);

                getActivity();
                ClipboardManager clipboard = (ClipboardManager) indoorActivity
                        .getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("", url);
                clipboard.setPrimaryClip(clip);

                Toast toast = Toast.makeText(indoorActivity,
                        "URL kopierad", Toast.LENGTH_LONG);
                toast.show();
            }
        });

        builder.setNeutralButton("Flytta punkt", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                IndoorMapMarker marker = indoorActivity.map.findMarkerById(ipId);
                if(marker != null) marker.setMoving(true);
                final Toast toast = Toast.makeText(indoorActivity, "Flytta punkten genom att dra den dit du önskar.", Toast.LENGTH_LONG);
                toast.show();
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                PointOfInterest point = new PointOfInterest(titleField.getText().toString(),
                        descriptionField.getText().toString(), spinner.getSelectedItem().toString(),
                        point1, point2, fireBaseIndoor.getFloor(), official.isChecked(), ipId);
                fireBaseIndoor.updateIp(point, Integer.parseInt(fireBaseIndoor.getFloor()));

                if (spinner.getSelectedItem().toString().equals("Hiss")){
                    //Ask to add the elevator on more floors
                    Toast toast = Toast.makeText(indoorActivity, R.string.createdElevator, Toast.LENGTH_LONG);
                    toast.show();
                }
                else if(spinner.getSelectedItem().toString().equals("Trappa")){
                    //Ask to add the stairs on more floors
                    Toast toast = Toast.makeText(indoorActivity, R.string.createdStairs, Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.create().show();
    }
}
