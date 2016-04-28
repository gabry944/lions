package com.example.micke.lions.indoor;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
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


public class AddPointDialogFragment extends DialogFragment {

    private String TAG = "AddPointDialogFragment";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        final Dialog dialogBuilder = new Dialog(getActivity());

        //Inflate the layout from xml
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.add_point_layout, null);
        dialogBuilder.setContentView(dialogView);

        final EditText title = (EditText) dialogView.findViewById(R.id.add_point_title);
        final EditText description = (EditText) dialogView.findViewById(R.id.add_point_description);
        final Spinner category = (Spinner) dialogView.findViewById(R.id.category_spinner);
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

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.category_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(adapter);


        //Get arguments - reference to firebase database
        Bundle bundle = this.getArguments();
        //final FireBaseIndoor fireBaseBuilding = (FireBaseIndoor) bundle.getSerializable("firebase");
        final FireBaseIndoor fireBaseIndoor = ((IndoorActivity) getActivity()).getFireBaseHandler();
        final float point1 = bundle.getFloat("lat", 0);
        final float point2 = bundle.getFloat("lng", 0);

        dialogBuilder.setTitle("Skapa ny intressepunkt!");

        Button submit = (Button) dialogView.findViewById(R.id.submit);
        Button cancel = (Button) dialogView.findViewById(R.id.cancel);
        final String ipId = fireBaseIndoor.generateId();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogBuilder.dismiss();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (title.getText().toString().equals("") || description.getText().toString().equals("") ||
                        category.getSelectedItem().toString().equals("Ingen kategori vald")) {
                    if (title.getText().toString().equals("")) {
                        title.setError("** Fyll i en titel");
                    }

                    if (description.getText().toString().equals("")) {
                        description.setError("** Fyll i beskrivning");
                    }
                    if (category.getSelectedItem().toString().equals("Ingen kategori vald")) {
                        ((TextView)category.getSelectedView()).setError("Fyll i kategori");
                    }
                    dialogBuilder.show();

                } else {
                    PointOfInterest point = new PointOfInterest(title.getText().toString(),
                            description.getText().toString(), category.getSelectedItem().toString(),
                            point1, point2, fireBaseIndoor.getFloor(), official.isChecked(), ipId);
                    fireBaseIndoor.updateIp(point, Integer.parseInt(fireBaseIndoor.getFloor()));

                    dialogBuilder.cancel();
                }
            }
        });

       return dialogBuilder;
    }
}
