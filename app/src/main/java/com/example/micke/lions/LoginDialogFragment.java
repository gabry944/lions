package com.example.micke.lions;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.micke.lions.indoor.IndoorActivity;
import com.example.micke.lions.outdoor.OutdoorActivity;

import java.util.ArrayList;
import java.util.List;


public class LoginDialogFragment extends DialogFragment {

    private IndoorActivity indoorActivity = null;
    private OutdoorActivity outdoorActivity = null;

    //Callback needed because firebase loads data slower than function return
    List<String[]> accounts = new ArrayList<>();

    private String TAG = "LoginDialogFragment";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        if(getActivity() instanceof IndoorActivity)
            indoorActivity = (IndoorActivity) getActivity();
        if(getActivity() instanceof OutdoorActivity)
            outdoorActivity = (OutdoorActivity) getActivity();

        FireBaseHandler fb = null;
        if(indoorActivity != null)
            fb = new FireBaseHandler(indoorActivity);
        else if(outdoorActivity != null)
            fb = new FireBaseHandler(outdoorActivity);
        if(fb != null)
            fb.getAdminAccount(this);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.login_layout, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);
        builder.setCancelable(false);
        builder.setTitle("Logga in med ett adminkonto");

        final EditText usernameField = (EditText) dialogView.findViewById(R.id.username);
        final EditText passwordField = (EditText) dialogView.findViewById(R.id.password);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.d(TAG, "onClick: authenticate");
                //Compare login details with the current admin accounts
                boolean authenticated = false;
                Log.d("hejadmin", "accounts = " + accounts.size());
                for(String[] acc : accounts) {
                    Log.d("hejadmin", "name = " + acc[0] + " pass = " + acc[1]);
                    if(usernameField.getText().toString().equals(acc[0]) &&
                            passwordField.getText().toString().equals(acc[1])) {
                        authenticated = true;
                        break;
                    }
                }

                if(authenticated){
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

    void setAccounts(List<String[]> l) {
        accounts = l;
    }
}
