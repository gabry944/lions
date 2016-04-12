package com.example.micke.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import me.dm7.barcodescanner.zbar.BarcodeFormat;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

/**
 * Created by iSirux on 2016-04-12.
 */
public class QRFragment extends Fragment implements ZBarScannerView.ResultHandler {

    private static ZBarScannerView mScannerView;
    private static final String ARG_SECTION_NUMBER = "section_number";

    public QRFragment() {

    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static QRFragment newInstance(int sectionNumber) {
        QRFragment fragment = new QRFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//            View rootView = inflater.inflate(R.layout.activity_qr_reader, container, false);
        ArrayList<BarcodeFormat> list = new ArrayList<>();
        list.add(BarcodeFormat.QRCODE);
        mScannerView = new ZBarScannerView(getContext());
        mScannerView.setFormats(list);
        View rootView = mScannerView;
        return rootView;
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Log.v("qr", rawResult.getContents()); // Prints scan results

        //Toast to show result
        Toast toast = Toast.makeText(getContext(),
                rawResult.getContents(), Toast.LENGTH_SHORT);
        toast.show();

        //
        String[] parts = rawResult.getContents().split("/");
        int partsLength = parts.length;

        for (String part: parts) {
            Log.d("buildparts", part);
        }

        if(partsLength > 1) {
            if(parts[0].equals("building")) {
                //Insert code for going to map-fragment here
                //parts[1] = building id
                //parts[2] = floors
                //parts[3] = floor id (1,2,3 etc.)
                //parts[4] = ips
                //parts[5] = ip id

                //Go to map fragment
                Intent intent = new Intent(getContext(), IndoorActivity.class);
                Bundle bundle = new Bundle();
                String ipId = "-1";
                if(parts[5] != null)
                    ipId = parts[5];
                bundle.putString("ipId", ipId);
                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else if(parts[0].equals("car")) {
                //Implement QR reading for car here
            }
        }

        // If you would like to resume scanning, call this method below:
        mScannerView.resumeCameraPreview(this);
    }
}
