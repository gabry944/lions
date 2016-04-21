package com.example.micke.lions.indoor;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.micke.lions.R;

import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import java.io.IOException;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.validation.Validator;

/**
 * Created by iSirux on 2016-04-11.
 */
public class AddPointDialogFragment extends DialogFragment {

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

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.cartegory_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(adapter);


        //Get arguments - reference to firebase database
        Bundle bundle = this.getArguments();
        //final FireBaseIndoor fireBaseBuilding = (FireBaseIndoor) bundle.getSerializable("firebase");
        final FireBaseIndoor fireBaseIndoor = ((IndoorActivity) getActivity()).getFireBaseHandler();
        final float point1 = bundle.getFloat("lat", 0);
        final float point2 = bundle.getFloat("lng", 0);
        final boolean[] done = {false};

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
                        category.getSelectedItem().toString().equals("")) {
                    if (title.getText().toString().equals("")) {
                        title.setError("** Fyll i en titel");
                    }

                    if (description.getText().toString().equals("")) {
                        description.setError("** Fyll i beskrivning");
                    }
                    if (category.getSelectedItem().toString().equals("")) {
                        // category.setError("** Lägg till en kategori");
                        Log.d("kategorierror", "Här kommer det funktionalitet sen");
                    }
                    dialogBuilder.show();

                } else {
                    done[0] = true;
                    PointOfInterest point = new PointOfInterest(title.getText().toString(),
                            description.getText().toString(), category.getSelectedItem().toString(), point1, point2, ipId);
                    fireBaseIndoor.updateIp(point, 4);
                    dialogBuilder.cancel();
                }
            }
        });

       return dialogBuilder;


    }
}

     /*   dialogBuilder
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the positive button event back to the host activity
//                            mListener.onDialogPositiveClick(NoticeDialogFragment.this);
                        //Save to database
                        String ipId = fireBaseIndoor.generateId();

                        if(!title.getText().toString().equals("") &&
                                !description.getText().toString().equals("")
                                && !category.getSelectedItem().toString().equals("")
                                ) {
                            PointOfInterest point = new PointOfInterest(title.getText().toString(),
<<<<<<< HEAD
                                    description.getText().toString(), category.getText().toString(), point1, point2, ipId);
                            fireBaseIndoor.updateIp(point, Integer.parseInt(fireBaseIndoor.getFloor()));
=======
                                    description.getText().toString(), category.getSelectedItem().toString(), point1, point2, ipId);
                            fireBaseIndoor.updateIp(point, 4);
>>>>>>> 0ab7565ca3086f7b7e1934480543ffcd6249a612
                        }
                        else{
                            AlertDialog dialog2 = dialogBuilder.create();
                            dialog2.show();
                            title.setError("fyll i alla fält");
                            //Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Fyll i alla fält", Toast.LENGTH_SHORT);
                            //toast.show();
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
    }*/

