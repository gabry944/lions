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

                final int action = MotionEventCompat.getActionMasked(event);

                float posX, posY;
                float curX, curY;

                float scalePointer1X, scalePointer1Y, scalePointer2X, scalePointer2Y;

                final float SCROLLSPEED = 1.0f;

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
                        //Ny plan: ta mittpunkt vid ACTION_DOWN och jämför avstånd (2 vektorer) i varje step i ACTION_MOVE
                        if (event.getPointerCount() == 2) {
                            Log.d("map_pointer", "1: pos: " + event.getX(0) + "," + event.getY(0));
                            Log.d("map_pointer", "2: pos: " + event.getX(1) + "," + event.getY(1));
                            Log.d("map_pointer", "scale: " + scaleFactor);
                            Log.d("map_pointer", "---");

                            scalePointer1X = event.getX(0) - mx;
                            scalePointer1Y = event.getY(0) - my;
                            scalePointer2X = event.getX(1) - mx2;
                            scalePointer2Y = event.getY(1) - my2;
                            double scalePointer = Math.sqrt(Math.pow(scalePointer1X + scalePointer2X, 2.0) + Math.pow(scalePointer1Y + scalePointer2Y, 2.0));
                            if (scalePointer > 10) scalePointer = 10;

                            if (scalePointer1X < 0 && scalePointer1Y < 0 && scalePointer2X > 0 && scalePointer2Y > 0) {
                                scaleFactor -= 0.01 * scalePointer;
                                Log.d("map_scale_pos", "1");
                            }
                            if ((scalePointer1X < 0 && scalePointer1Y > 0 && scalePointer2X > 0 && scalePointer2Y < 0)) {
                                scaleFactor -= 0.01 * scalePointer;
                                Log.d("map_scale_pos", "2");
                            }
                            if ((scalePointer1X > 0 && scalePointer1Y > 0 && scalePointer2X < 0 && scalePointer2Y < 0)) {
                                scaleFactor += 0.01 * scalePointer;
                                Log.d("map_scale_pos", "3");
                            }
                            if ((scalePointer1X > 0 && scalePointer1Y < 0 && scalePointer2X < 0 && scalePointer2Y > 0)) {
                                scaleFactor += 0.01 * scalePointer;
                                Log.d("map_scale_pos", "4");
                            }

//                                    scaleFactor -= 0.01 * scalePointer;
//                                    Log.d("map_scale", "down -> " + scalePointer);
//                                } else {
//                                    scaleFactor += 0.01 * scalePointer;
//                                    Log.d("map_scale", "up -> " + scalePointer);
//                                }

                            scaleFactor = (scaleFactor > 10.0f) ? 10.0f : scaleFactor;
                            scaleFactor = (scaleFactor < 1.0f) ? 1.0f : scaleFactor;
                            switcherView.setScaleX(scaleFactor);
                            switcherView.setScaleY(scaleFactor);
                            mx = event.getX(0);
                            my = event.getY(0);
                            mx2 = event.getX(1);
                            my2 = event.getY(1);

                            break;  //we don't want to zoom and move at the same time
                        }

                        curX = event.getX();
                        curY = event.getY();
                        Log.d("map_move", "---");
                        Log.d("map_move", "event.getX() = " + Float.toString(event.getX()) + " event.getY() = " + Float.toString(event.getY()));
                        Log.d("map_move", "getScaleX() = " + Float.toString(switcherView.getScaleX()) + " getScaleY() = " + Float.toString(switcherView.getScaleY()));
                        Log.d("map_move", "getTranslateionX() = " + Float.toString(switcherView.getTranslationX()) + " getTranslateionY() = " + Float.toString(switcherView.getTranslationY()));
                        //switcherView.scrollBy((int) (mx - curX), (int) (my - curY));
                        posX = switcherView.getTranslationX();
                        posY = switcherView.getTranslationY();
                        float deltaX = Math.abs(mx - curX) < 5.0f ? (mx - curX) : Math.signum((mx - curX)) * 5.0f;
                        float deltaY = Math.abs(my - curY) < 5.0f ? (my - curY) : Math.signum((my - curY)) * 5.0f;
                        switcherView.setTranslationX(posX - SCROLLSPEED * deltaX);
                        switcherView.setTranslationY(posY - SCROLLSPEED * deltaY);
                        mx = curX;
                        my = curY;
                        break;
                    case MotionEvent.ACTION_POINTER_UP:

//                        if (event.getPointerCount() == 2) {
//                            Log.d("map_pointer", "1: pos: " + event.getX(0) + "," + event.getY(0));
//                            Log.d("map_pointer", "2: pos: " + event.getX(1) + "," + event.getY(1));
//                            Log.d("map_pointer", "scale: " + scaleFactor);
//                            Log.d("map_pointer", "---");
//
//                            scalePointer1X = event.getX(0) - mx;
//                            scalePointer1Y = event.getY(0) - my;
//                            scalePointer2X = event.getX(1) - mx2;
//                            scalePointer2Y = event.getY(1) - my2;
//                            double scalePointer = Math.pow(scalePointer1X + scalePointer2X, 2.0) + Math.pow(scalePointer1Y + scalePointer2Y, 2.0);
//                            if(scalePointer > 40) scalePointer = 40;
//
//                            if (scaleFactor > 0.2 && scalePointer1X * scalePointer1Y <= 0 && scalePointer2X * scalePointer2Y <= 0) { // same sign , same sign
//                                scaleFactor -= 0.01*scalePointer;
//                                Log.d("map_scale", "down -> " + scalePointer);
//                            }
//                            if (scaleFactor < 5.0 && scalePointer1X * scalePointer1Y >= 0 && scalePointer2X * scalePointer2Y >= 0) { // different sign , different sign
//                                scaleFactor += 0.01*scalePointer;
//                                Log.d("map_scale", "up -> " + scalePointer);
//                            }
//
//                            //Log.d("map_scale", "-> " + scalePointer);
//
//                            scalePointer = 0;
//                            if (scalePointer < -10.0 && scaleFactor < 5.0) {
//                                Log.d("map_scale", "scale up");
//                                scaleFactor += 0.1;
//                            }
//
//                            if (scalePointer > 10.0 && scaleFactor > 0.2) {
//                                Log.d("map_scale", "scale down");
//                                scaleFactor -= 0.1;
//                            }
//
//                            switcherView.setScaleX(scaleFactor);
//                            switcherView.setScaleY(scaleFactor);
//
//                            mx = event.getX(0);
//                            my = event.getY(0);
//                            mx2 = event.getX(1);
//                            my2 = event.getY(1);
//
//                            break;  //we don't want to zoom and move at the same time
//                        }

                        break;
                }

                return true;
            }
        });


        return rootView;
    }
}
