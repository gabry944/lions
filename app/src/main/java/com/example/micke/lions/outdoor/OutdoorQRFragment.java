package com.example.micke.lions.outdoor;

import android.app.DialogFragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.micke.lions.R;
import com.example.micke.lions.indoor.IndoorActivity;

import java.util.ArrayList;

import me.dm7.barcodescanner.zbar.BarcodeFormat;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

/**
 * Created by iSirux on 2016-04-12.
 */
public class OutdoorQRFragment extends Fragment implements ZBarScannerView.ResultHandler, FragmentResolver {

    private static ZBarScannerView mScannerView;
    private static final String ARG_SECTION_NUMBER = "section_number";
    private FireBaseOutdoor fireBaseHandler;

    public OutdoorQRFragment() {
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
    public static OutdoorQRFragment newInstance(int sectionNumber) {
        OutdoorQRFragment fragment = new OutdoorQRFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fireBaseHandler = ((OutdoorActivity) getActivity()).getFireBaseHandler();
        View view = inflater.inflate(R.layout.fragment_outdoor_qr, container, false);
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.qr_linear_layout);

        ArrayList<BarcodeFormat> list = new ArrayList<>();
        list.add(BarcodeFormat.QRCODE);
        mScannerView = new ZBarScannerView(getContext());
        mScannerView.setFormats(list);
        View scannerView = mScannerView;
        linearLayout.addView(scannerView);

        final FloatingActionButton fab = (FloatingActionButton) linearLayout.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Car car = new Car("car name", fireBaseHandler.generateId(), 0, 0);
                fireBaseHandler.newCar(car);

                String url = "http://api.qrserver.com/v1/create-qr-code/?color=000000&bgcolor=FFFFFF&data=" +
                        "car/" + car.getId()
                        + "&qzone=1&margin=0&size=400x400&ecc=L";

                ClipboardManager clipboard = (ClipboardManager) getActivity()
                        .getSystemService(getActivity().CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("", url);
                clipboard.setPrimaryClip(clip);

                Toast toast = Toast.makeText(getContext(),
                        "QR code URL copied to clipboard", Toast.LENGTH_LONG);
                toast.show();

                Log.d("fab", "clicked");
            }
        });

        return linearLayout;
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
                fireBaseHandler.getCar(this, parts[1]);
            }
        }

        // If you would like to resume scanning, call this method below:
        mScannerView.resumeCameraPreview(this);
    }

    @Override
    public void startCarDialog(Car car) {
        if(((OutdoorActivity) getActivity()).getViewPager().getCurrentItem() == 2) {
            DialogFragment newFragment = new CarDialogFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("car", car);
            newFragment.setArguments(bundle);
            newFragment.show(getActivity().getFragmentManager(), "car_dialog_fragment");
        }
    }
}