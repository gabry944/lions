package com.example.micke.lions.outdoor;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.example.micke.lions.R;

public class CreateCarDialogFragment extends DialogFragment {

    FireBaseOutdoor fireBaseHandler;
    Car car;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

        fireBaseHandler = ((OutdoorActivity) getActivity()).getFireBaseHandler();

        //Inflate the layout from xml
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.create_car_dialog_fragment, null);
        dialogBuilder.setView(dialogView);

//        Button findCar = (Button) dialogView.findViewById(R.id.find_car);
//        Button parkCar = (Button) dialogView.findViewById(R.id.park_car);
//        Button cancel = (Button) dialogView.findViewById(R.id.cancel);
//        TextView permissionMessage = (TextView) dialogView.findViewById(R.id.permission);

        dialogBuilder
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the positive button event back to the host activity
//                            mListener.onDialogPositiveClick(NoticeDialogFragment.this);
                        //Save to database
                        car = new Car("Bil", fireBaseHandler.generateId(), 0, 0);
                        fireBaseHandler.updateCar(car);

                        String url = "http://api.qrserver.com/v1/create-qr-code/?color=000000&bgcolor=FFFFFF&data=" +
                                "car/" + car.getId()
                                + "&qzone=1&margin=0&size=400x400&ecc=L";

                        ClipboardManager clipboard = (ClipboardManager) getActivity()
                                .getSystemService(getActivity().CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("", url);
                        clipboard.setPrimaryClip(clip);

                        Toast toast = Toast.makeText(getActivity(),
                                "URL kopierad", Toast.LENGTH_LONG);
                        toast.show();
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
