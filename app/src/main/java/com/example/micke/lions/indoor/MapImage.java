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
public class MapImage extends RelativeLayout {

    private float mScaleFactor = 1.f;
    private ScaleGestureDetector mScaleDetector;
    private GestureDetector mGestureDetector;
    private RelativeLayout relativeLayout;
    private ImageView imageView;
    private float imageWidth, imageHeight, imageYOffset;
    private IndoorMapFragment mIndoorMapFragment;

    public MapImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
        mGestureDetector = new GestureDetector(getContext(), new PanListener());
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.scale_test, this);
        this.imageView = (ImageView) findViewById(R.id.imageView);

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
                return true;
            }
        });
    }

    public boolean onTouchEvent(MotionEvent event) {
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

    public RelativeLayout getRelativeLayout() {
        return relativeLayout;
    }

    //Resets the view
    public void resetView() {
        mScaleFactor = 1;
        relativeLayout.setScaleX(mScaleFactor);
        relativeLayout.setScaleY(mScaleFactor);

        relativeLayout.setTranslationX(0);
        relativeLayout.setTranslationY(0);
    }

    private class ScaleListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            //Adjust scale factor
            mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.8f, Math.min(mScaleFactor, 5.0f));

            //Scale layout with scale factor
            relativeLayout.setScaleX(mScaleFactor);
            relativeLayout.setScaleY(mScaleFactor);

            //Invalidate the layout while scaling
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
/*
            Log.d("touch", "touchX: " + touchX + " touchY: " + touchY);
            Log.d("touch", "viewcoords[0]: " + viewCoords[0] + " viewcoords[1]: " + viewCoords[1]);
            Log.d("touch", "relativeLayout.getScaleX(): " + relativeLayout.getScaleX() + " relativeLayout.getScaleY(): " + relativeLayout.getScaleY());
*/
            //Touched point on imageview in pixels
            imageX = (touchX - viewCoords[0]) / relativeLayout.getScaleX();
            imageY = (touchY - viewCoords[1] + imageYOffset) / relativeLayout.getScaleY();

            //Normalize value - min 0, max 1
            point[0] = Math.max(0f, Math.min(1f, imageX / imageWidth));
            point[1] = Math.max(0f, Math.min(1f, imageY / imageHeight));

            //Log.d("touch", "point[0]: " + point[0] + " point[1]: " + point[1]);

            //Add a point on the image at touched point
            mIndoorMapFragment.showAddPointDialog(point);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            //Get the relative layout's x and y translation
            top = relativeLayout.getTranslationY();
            left = relativeLayout.getTranslationX();

            //Calculate the new translation
            translationX = left - distanceX;
            translationY = top - distanceY;

            //Apply the new translation
            relativeLayout.setTranslationX(translationX);
            relativeLayout.setTranslationY(translationY);
            return true;
        }
    }

    public float[] convertCoordinatesPercent(float x, float y) {
        float[] point = new float[2];

        int[] viewCoords = new int[2];
        imageView.getLocationOnScreen(viewCoords);
        imageYOffset = viewCoords[1];

        //Normalize value - min 0, max 1
        point[0] = Math.max(0f, Math.min(1f, x / imageWidth));
        point[1] = Math.max(0f, Math.min(1f, (y) / imageHeight));

        return point;
    }

    public float[] convertCoordinates(float percentageX, float percentageY) {
        float[] point = new float[2];
        int[] viewCoords = new int[2];

        //Get the image view's location on the screen
        imageView.getLocationOnScreen(viewCoords);

        //Top left corner
        point[0] = (percentageX * imageWidth );
        point[1] = (percentageY * imageHeight );


        Log.d("touch", "x: " + point[0] + " percentageX: " + percentageX + " xscale: " + relativeLayout.getScaleX()
                + " imagewidth: " + imageWidth + " viewcoords[0]: " + viewCoords[0]);

        Log.d("touch", "y: " + point[1] + " percentageY: " + percentageY + " yscale: " + relativeLayout.getScaleY()
                + " imageheight: " + imageHeight + " viewcoords[1]: " + viewCoords[1]);

        return point;
    }

    public float getScaleFactor(){
        return mScaleFactor;
    }
}
