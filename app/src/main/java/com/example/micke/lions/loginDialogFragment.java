package com.example.micke.lions;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.menu.MenuView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.micke.lions.indoor.FireBaseIndoor;
import com.example.micke.lions.indoor.IndoorActivity;
import com.example.micke.lions.indoor.IndoorMapMarker;
import com.example.micke.lions.indoor.PointOfInterest;
import com.example.micke.lions.outdoor.OutdoorActivity;


public class loginDialogFragment extends DialogFragment {

    private IndoorActivity indoorActivity = null;
    private OutdoorActivity outdoorActivity = null;

    private String TAG = "loginDialogFragment";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        if(getActivity() instanceof IndoorActivity)
            indoorActivity = (IndoorActivity) getActivity();
        if(getActivity() instanceof OutdoorActivity)
            outdoorActivity = (OutdoorActivity) getActivity();

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.login_layout, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);
        builder.setCancelable(false);
        builder.setTitle("Ändra punkt");

        final EditText usernameField = (EditText) dialogView.findViewById(R.id.username);
        final EditText passwordField = (EditText) dialogView.findViewById(R.id.password);
        usernameField.setText("admin", TextView.BufferType.EDITABLE);
        passwordField.setText("qwerty", TextView.BufferType.EDITABLE);
        passwordField.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.d(TAG, "onClick: authenticate");
                //TODO get admin fields from firebase instead
                if(usernameField.getText().toString().equals("admin") &&
                        passwordField.getText().toString().equals("qwerty")) {
                    if(indoorActivity != null) {
                        Common.MakeAdmin(indoorActivity.map, indoorActivity.list, indoorActivity.qr);
                        Common.setAdminButton(indoorActivity.adminButton, indoorActivity);
                    }
                    else if(outdoorActivity != null) {
                        Common.MakeAdmin(outdoorActivity.map, outdoorActivity.list, outdoorActivity.qr);
                        Common.setAdminButton(outdoorActivity.adminButton, outdoorActivity);
                    }
                }
                else {
                    Toast toast = null;
                    if(indoorActivity != null)
                        toast = Toast.makeText(indoorActivity, "Fel användarnamn eller lösenord", Toast.LENGTH_SHORT);
                    else if(outdoorActivity != null)
                        toast = Toast.makeText(outdoorActivity, "Fel användarnamn eller lösenord", Toast.LENGTH_SHORT);
                    toast.show();
                }

            }
        });
        builder.setNegativeButton(R.string.abort, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.d(TAG, "onClick: avbryt");
            }
        });

        // Create the AlertDialog object and return it
        return builder.create();
    }
}
