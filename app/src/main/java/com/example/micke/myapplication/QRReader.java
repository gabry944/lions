package com.example.micke.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class QRReader extends AppCompatActivity implements ZBarScannerView.ResultHandler {
    private ZBarScannerView mScannerView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZBarScannerView(this);    // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view
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

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Log.v("qr", rawResult.getContents()); // Prints scan results

        //Toast to show result
        Toast toast = Toast.makeText(getApplicationContext(),
                rawResult.getContents(), Toast.LENGTH_SHORT);
        toast.show();

        //
        String[] parts = rawResult.getContents().split("/");
        int partsLength = parts.length;
        if(partsLength > 1) {
            if(parts[0].equals("building")) {
                //Insert code for going to map-fragment here
                //parts[1] = bulding id
                //parts[2] = floors
                //parts[3] = floor id (1,2,3 etc.)
                //parts[4] = ips
                //parts[5] = ip id
            }
        }

        // If you would like to resume scanning, call this method below:
        mScannerView.resumeCameraPreview(this);
    }
}
