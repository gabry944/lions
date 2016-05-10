package com.example.micke.lions.indoor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.micke.lions.InloggChange;
import com.example.micke.lions.R;

import java.util.ArrayList;

import me.dm7.barcodescanner.zbar.BarcodeFormat;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class IndoorQRFragment extends Fragment implements ZBarScannerView.ResultHandler,InloggChange {

    String TAG = "IndoorQRFragment";
    private static ZBarScannerView mScannerView;
    private static final String ARG_SECTION_NUMBER = "section_number";
    private ImageButton goToList;

    public IndoorQRFragment() {
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
    public void onStop() {
        super.onStop();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static IndoorQRFragment newInstance(int sectionNumber) {
        IndoorQRFragment fragment = new IndoorQRFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_indoor_qr, container, false);
        RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.qr_linear_layout);

        goToList = (ImageButton) view.findViewById(R.id.goToIndoorList2);
        ArrayList<BarcodeFormat> list = new ArrayList<>();
        list.add(BarcodeFormat.QRCODE);

        mScannerView = (ZBarScannerView) view.findViewById(R.id.zBarScannerIndoor);
        mScannerView.setFormats(list);

        goToList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewPager mPager = (ViewPager) v.getRootView().findViewById(R.id.container);
                mPager.setCurrentItem(1, true);
            }
        });

        return relativeLayout;
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
            //send data to OutdoorActivity
            Intent intent = new Intent();
            intent.putExtra("data", parts);
            intent.putExtra("goalID", ((IndoorActivity)getActivity()).map.getGoal().getId());
            intent.putExtra("goalFloor", ((IndoorActivity)getActivity()).map.getGoal().getFloor());
            getActivity().setResult(Activity.RESULT_OK, intent);
            getActivity().finish();
        }

        // If you would like to resume scanning, call this method below:
        mScannerView.resumeCameraPreview(this);
    }

    @Override
    public void adminInlogg() {
        Log.d(TAG, "adminInlogg: ");
    }

    @Override
    public void commonInlogg() {
        Log.d(TAG, "commonInlogg: ");
    }
}
