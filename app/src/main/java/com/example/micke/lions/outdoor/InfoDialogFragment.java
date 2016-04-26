package com.example.micke.lions.outdoor;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.example.micke.lions.R;

/**
 * Created by mikaela on 16-04-26.
 */
public class InfoDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

        //Inflate the layout from xml
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.info_dialog, null);
        dialogBuilder.setView(dialogView);

        dialogBuilder
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        return dialogBuilder.create();
    }
}
