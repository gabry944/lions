package com.example.micke.lions.indoor;

import android.app.DialogFragment;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.view.ScaleGestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.micke.lions.R;

/**
 * Created by iSirux on 2016-04-21.
 */
public class ScaleTest extends RelativeLayout {

    private float mScaleFactor = 1.f;
    private ScaleGestureDetector mScaleDetector;
    private GestureDetector mGestureDetector;
    private RelativeLayout relativeLayout;
    private ImageView imageView;
    private View mView;
    private float mX, mY;

    public ScaleTest(Context context, AttributeSet attrs) {
        super(context, attrs);
//        mView = new View(context, attrs);
//        imageView = new ImageView(context, attrs);
//        imageView = (ImageView) findViewById(R.id.map);
        mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
        mGestureDetector = new GestureDetector(getContext(), new PanListener());
        Log.d("scale", "constructed with context/attrs");
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.scale_test, this);
        this.imageView = (ImageView) findViewById(R.id.imageView);
        mX = mY = 0;

//        imageView.setOnTouchListener(new OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                final int action = event.getAction();
//                switch (action & MotionEvent.ACTION_MASK) {
//                    case MotionEvent.ACTION_DOWN:
//                        mX = event.getX();
//                        mY = event.getY();
//                        break;
//                }
//                return false;
//            }
//        });

//        imageView.setOnLongClickListener(new View.OnLongClickListener() {
//            public boolean onLongClick(View arg0) {
//                Log.d("scale", "Long click @ " + mX + " " + mY);
////                    addPoint(r, mx - r.getWidth() / 2,
////                            my - r.getHeight() / 2 + 60);
//
////                    float[] point = new float[2];
////                    point[0] = mx - r.getWidth() / 2;
////                    point[1] = my - r.getHeight() / 2 + 60;
////                    DialogFragment newFragment = new AddPointDialogFragment();
////                    Bundle bundle = new Bundle();
////                    bundle.putFloat("lat", point[0]);
////                    bundle.putFloat("lng", point[1]);
////                    newFragment.setArguments(bundle);
////                    newFragment.show(indoorActivity.getFragmentManager(), "add_point_layout");
//                return false;
//            }
//        });
    }

    public boolean onTouchEvent(MotionEvent event) {
        //Get coordinates for finger
        mX = event.getX();
        mY = event.getY();

        boolean retVal = mScaleDetector.onTouchEvent(event);
        retVal = mGestureDetector.onTouchEvent(event) || retVal;
        return retVal || super.onTouchEvent(event);
    }


    public void setImage(BitmapDrawable bitmapDrawable) {
        imageView.setImageDrawable(bitmapDrawable);
    }

    public void setParent(RelativeLayout relativeLayout) {
        this.relativeLayout = relativeLayout;
    }

    private class ScaleListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.8f, Math.min(mScaleFactor, 5.0f));

            relativeLayout.setScaleX(mScaleFactor);
            relativeLayout.setScaleY(mScaleFactor);
//            Log.d("scale", "onScale - scalefactor: " + mScaleFactor);
            invalidate();
            return true;
        }
    }

    private class PanListener
            extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            float top = relativeLayout.getTranslationY();
            float left = relativeLayout.getTranslationX();

            float translationX = left - distanceX;
            float translationY = top - distanceY;

            relativeLayout.setTranslationX(translationX);
            relativeLayout.setTranslationY(translationY);
            return true;
        }
    }
}
