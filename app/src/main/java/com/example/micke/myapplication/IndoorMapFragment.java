package com.example.micke.myapplication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * A placeholder fragment containing a simple view.
 */
public class IndoorMapFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */

    private float mx, mx2;  //2 is for the second finger. Used for zooming
    private float my, my2;
    private float scaleFactor = 5.0f;

    private static final String ARG_SECTION_NUMBER = "section_number";

    public IndoorMapFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static IndoorMapFragment newInstance(int sectionNumber) {
        IndoorMapFragment fragment = new IndoorMapFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_indoor_map, container, false);

        final ImageView switcherView = (ImageView) rootView.findViewById(R.id.map);
        switcherView.setScaleX(5.0f);
        switcherView.setScaleY(5.0f);

        switcherView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View arg0, MotionEvent event) {
                float posX, posY;
                float curX, curY;

                final float SCROLLSPEED = 30.0f;
                final float ZOOMSPEED = 15.0f;

                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        mx = event.getX();
                        my = event.getY();

                        //Special case for two fingers
                        if (event.getPointerCount() == 2) {
                            mx2 = event.getX(1);
                            my2 = event.getY(1);
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:

                        //Check if user want to zoom
                        if (event.getPointerCount() == 2) {

                            float zoomVectorX = mx - mx2;
                            float zoomVectorY = my - my2;
                            float newZoomVectorX = event.getX(0) - event.getX(1);
                            float newZoomVectorY = event.getY(0) - event.getY(1);

                            double diff = ((double)scaleFactor/5.0) *
                                    (Math.sqrt(Math.pow(newZoomVectorX, 2.0) + Math.pow(newZoomVectorY, 2.0)) -
                                    Math.sqrt(Math.pow(zoomVectorX, 2.0) + Math.pow(zoomVectorY, 2.0)));

                            diff = (diff < -ZOOMSPEED) ? -ZOOMSPEED : diff;
                            diff = (diff > ZOOMSPEED) ? ZOOMSPEED : diff;
                            scaleFactor += 0.01 * diff;
                            scaleFactor = (scaleFactor > 10.0f) ? 10.0f : scaleFactor;
                            scaleFactor = (scaleFactor < 1.0f) ? 1.0f : scaleFactor;

                            Log.d("map_indoor", "Two fingers: scaleFactor = " + scaleFactor + ", diff = " + diff);
                            switcherView.setScaleX(scaleFactor);
                            switcherView.setScaleY(scaleFactor);
                            mx = event.getX(0);
                            my = event.getY(0);
                            mx2 = event.getX(1);
                            my2 = event.getY(1);
                        }
                        //Check if user want to drag the map
                        else if (event.getPointerCount() == 1) {
                            curX = event.getX();
                            curY = event.getY();

                            posX = switcherView.getTranslationX();
                            posY = switcherView.getTranslationY();
                            float deltaX = (scaleFactor/2.0f)*Math.abs(mx - curX) < SCROLLSPEED ? (scaleFactor/2.0f)*(mx - curX) : Math.signum((mx - curX)) * SCROLLSPEED;
                            float deltaY = (scaleFactor/2.0f)*Math.abs(my - curY) < SCROLLSPEED ? (scaleFactor / 2.0f) * (my - curY) : Math.signum((my - curY)) * SCROLLSPEED;

                            Log.d("map_indoor", "One finger: deltaX = " + deltaX + ", deltaY = " + deltaY);
                            switcherView.setTranslationX(posX - deltaX);
                            switcherView.setTranslationY(posY - deltaY);
                            mx = curX;
                            my = curY;
                        }
                }

                return true;
            }
        });

        return rootView;
    }
}
