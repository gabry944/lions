package com.example.micke.lions.indoor;

import android.app.Dialog;
import android.app.DialogFragment;
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

import com.example.micke.lions.Common;
import com.example.micke.lions.R;

import org.w3c.dom.Text;


public class ChangePointDialogFragment extends DialogFragment {

    private String TAG = "ChangePointDialogFragment";
    private IndoorActivity indoorActivity;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //get witch point to remove
        Bundle bundle = this.getArguments();
        final String point = bundle.getString("id");

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.removePointQuetion)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d(TAG, "onClick: ta bort");

                        //tell IndoorMapFragment to remove the ip
                        indoorActivity = (IndoorActivity) getActivity();
                        indoorActivity.map.RemovePoint(point);
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d(TAG, "onClick: behåll");
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
