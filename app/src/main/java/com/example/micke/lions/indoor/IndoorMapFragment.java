package com.example.micke.lions.indoor;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.micke.lions.R;

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

        //final MyImageView switcherView;

        final RelativeLayout r = (RelativeLayout) rootView.findViewById(R.id.mapLayout);
        final ImageView switcherView = (ImageView) rootView.findViewById(R.id.map);
        //r.setScaleX(5.0f);
        //r.setScaleY(5.0f);

        //List<PointOfInterest> l = ((IndoorActivity) getActivity()).getData();
        //addPoint(r, 1000 * (float) Math.random(), 1000 * (float) Math.random());
        //addPoint(r, 1000 * (float) Math.random(), 1000 * (float) Math.random());
        //addPoint(r, 1000 * (float) Math.random(), 1000 * (float) Math.random());


        r.setOnTouchListener(new View.OnTouchListener() {

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
                            //r.setScaleX(scaleFactor);
                            //r.setScaleY(scaleFactor);
                            mx = event.getX(0);
                            my = event.getY(0);
                            mx2 = event.getX(1);
                            my2 = event.getY(1);
                        }
                        //Check if user want to drag the map
                        else if (event.getPointerCount() == 1) {
                            curX = event.getX();
                            curY = event.getY();

                            posX = r.getTranslationX();
                            posY = r.getTranslationY();
//                            float deltaX = (scaleFactor/2.0f)*Math.abs(mx - curX) < SCROLLSPEED ? (scaleFactor/2.0f)*(mx - curX) : Math.signum((mx - curX)) * SCROLLSPEED;
//                            float deltaY = (scaleFactor/2.0f)*Math.abs(my - curY) < SCROLLSPEED ? (scaleFactor / 2.0f) * (my - curY) : Math.signum((my - curY)) * SCROLLSPEED;
                            float deltaX = mx-curX;
                            float deltaY = my-curY;

                            //Log.d("map_indoor", "One finger: deltaX = " + deltaX + ", deltaY = " + deltaY);
                            Log.d("map_indoor", "posX = " + event.getRawX() + ", posY = " + event.getRawY());
                            Log.d("map_indoor", "transX = " + r.getTranslationX() + ", transY = " + r.getTranslationY());
//                            float tempx = event.getRawX();
//                            float tempy = event.getRawY();

                            int[] viewCoords = new int[2];
                            r.getLocationOnScreen(viewCoords);
                            int imageX = viewCoords[0];
                            int imageY = viewCoords[1];

                            //This coordinate transformation should be moved into its own function
                            addPoint(r, event.getRawX()-r.getWidth()/2-r.getTranslationX(), event.getRawY()-r.getHeight()/2-100-r.getTranslationY());

                            r.setTranslationX(posX - deltaX);
                            r.setTranslationY(posY - deltaY);
                            mx = curX;
                            my = curY;
                        }
                }

                return true;
            }
        });

        return rootView;
    }

    public void highlightIP(String ipID) {
        Log.d("IndoorMapFragment", "highlightIP: ipID = " + ipID);
    }

    private void addPoint(RelativeLayout parent, final float posX, final float posY) {
        ImageView point = new ImageView(getContext());
        point.setX(posX);
        point.setY(posY);
        point.setScaleX(0.05f);
        point.setScaleY(0.05f);
        point.setImageResource(R.drawable.map_marker);
        parent.addView(point);

        point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "Klickar på pungtjävel " + "posX = " + posX  + " posY = " + posY );
            }
        });
    }




/*
    public class MyImageView extends ImageView {
        private ScaleGestureDetector mScaleDetector;
        private static final int MAX_SIZE = 1024;

        private static final String TAG = "MyImageView";
        PointF DownPT = new PointF(); // Record Mouse Position When Pressed Down
        PointF StartPT = new PointF(); // Record Start Position of 'img'

        public MyImageView(Context context) {
            super(context);
            mScaleDetector = new ScaleGestureDetector(context, new MySimpleOnScaleGestureListener());
            setBackgroundColor(Color.RED);
            setScaleType(ScaleType.MATRIX);
            setAdjustViewBounds(true);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

            lp.setMargins(-MAX_SIZE, -MAX_SIZE, -MAX_SIZE, -MAX_SIZE);
            this.setLayoutParams(lp);
            this.setX(MAX_SIZE);
            this.setY(MAX_SIZE);

        }

        int firstPointerID;
        boolean inScaling = false;

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            // get pointer index from the event object
            int pointerIndex = event.getActionIndex();
            // get pointer ID
            int pointerId = event.getPointerId(pointerIndex);
            //First send event to scale detector to find out, if it's a scale
            boolean res = mScaleDetector.onTouchEvent(event);

            if (!mScaleDetector.isInProgress()) {
                int eid = event.getAction();
                switch (eid & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_MOVE:
                        if (pointerId == firstPointerID) {

                            PointF mv = new PointF((int) (event.getX() - DownPT.x), (int) (event.getY() - DownPT.y));

                            this.setX((int) (StartPT.x + mv.x));
                            this.setY((int) (StartPT.y + mv.y));
                            StartPT = new PointF(this.getX(), this.getY());

                        }
                        break;
                    case MotionEvent.ACTION_DOWN: {
                        firstPointerID = pointerId;
                        DownPT.x = (int) event.getX();
                        DownPT.y = (int) event.getY();
                        StartPT = new PointF(this.getX(), this.getY());
                        break;
                    }
                    case MotionEvent.ACTION_POINTER_DOWN: {
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        firstPointerID = -1;
                        break;
                    }
                    default:
                        break;
                }
                return true;
            }
            return true;

        }

        public boolean onScaling(ScaleGestureDetector detector) {

            this.setScaleX(this.getScaleX() * detector.getScaleFactor());
            this.setScaleY(this.getScaleY() * detector.getScaleFactor());
            invalidate();
            return true;
        }

        private class MySimpleOnScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {


            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                return onScaling(detector);
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                Log.d(TAG, "onScaleBegin");
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector arg0) {
                Log.d(TAG, "onScaleEnd");
            }
        }
    }
*/
}
