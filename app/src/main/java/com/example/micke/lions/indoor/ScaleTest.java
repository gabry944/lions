package com.example.micke.lions.indoor;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewTreeObserver;
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
    private float mX, mY;
    private float imageWidth, imageHeight, imageYOffset;
    private IndoorMapFragment mIndoorMapFragment;

    public ScaleTest(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
        mGestureDetector = new GestureDetector(getContext(), new PanListener());
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.scale_test, this);
        this.imageView = (ImageView) findViewById(R.id.imageView);
        mX = mY = 0;

        //Get measurements of image
        ViewTreeObserver vto = imageView.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                imageView.getViewTreeObserver().removeOnPreDrawListener(this);
                imageHeight = imageView.getMeasuredHeight();
                imageWidth = imageView.getMeasuredWidth();
                int[] viewCoords = new int[2];
                imageView.getLocationOnScreen(viewCoords);
                imageYOffset = viewCoords[1];
                Log.d("touch", "onPreDraw, imageYOffset: " + imageYOffset);
                return true;
            }
        });
    }

    public boolean onTouchEvent(MotionEvent event) {
//        Log.d("touch", "onTouchEvent!");
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

    public void setCallback(IndoorMapFragment indoorMapFragment) {
        mIndoorMapFragment = indoorMapFragment;
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

        float[] point = new float[2];
        int[] viewCoords = new int[2];
        float touchX, touchY;
        float imageX, imageY;
        float top, left;
        float translationX, translationY;

        @Override
        public void onLongPress(MotionEvent e) {
            //Get the image location on screen (top/left)
            imageView.getLocationOnScreen(viewCoords);

            //Touched point on screen
            touchX = e.getX();
            touchY = e.getY();

            //Touched point on imageview in pixels
            imageX = (touchX - viewCoords[0]) / relativeLayout.getScaleX();
            imageY = (touchY - viewCoords[1] + imageYOffset) / relativeLayout.getScaleY();

            //Normalize value - min 0, max 1
            point[0] = Math.max(0f, Math.min(1f, imageX / imageWidth));
            point[1] = Math.max(0f, Math.min(1f, imageY / imageHeight));

            //Add a point on the image at touched point
//            mIndoorMapFragment.showAddPointDialog(point);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            top = relativeLayout.getTranslationY();
            left = relativeLayout.getTranslationX();

            translationX = left - distanceX;
            translationY = top - distanceY;

            relativeLayout.setTranslationX(translationX);
            relativeLayout.setTranslationY(translationY);
            return true;
        }
    }
}
