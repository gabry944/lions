package com.example.micke.myapplication;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by iSirux on 2016-04-12.
 */
public class QRFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";

    public QRFragment() {
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
        View rootView = inflater.inflate(R.layout.activity_qr_reader, container, false);
        return rootView;
    }
}
