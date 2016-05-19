package com.example.micke.lions.outdoor;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

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

        ImageView rightArrow =(ImageView) dialogView.findViewById(R.id.right_arrow);
        ImageView leftArrow = (ImageView) dialogView.findViewById(R.id.left_arrow);

        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.pulse_button);
        rightArrow.startAnimation(animation);
        leftArrow.startAnimation(animation);

        TextView textView1 = (TextView) dialogView.findViewById(R.id.info_text1);
        TextView textView2 = (TextView) dialogView.findViewById(R.id.info_text2);
        TextView textView3 = (TextView) dialogView.findViewById(R.id.info_text3);
        TextView textView4 = (TextView) dialogView.findViewById(R.id.info_text4);
        TextView textView5 = (TextView) dialogView.findViewById(R.id.info_text5);
        TextView textView6 = (TextView) dialogView.findViewById(R.id.info_text6);
        Typeface myCustomFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSans-Regular.ttf");
        textView1.setTypeface(myCustomFont);
        textView2.setTypeface(myCustomFont);
        textView3.setTypeface(myCustomFont);
        textView4.setTypeface(myCustomFont);
        textView5.setTypeface(myCustomFont);
        textView6.setTypeface(myCustomFont);

        dialogBuilder
                .setPositiveButton("Jag förstår", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        return dialogBuilder.create();
    }
}
