package com.example.micke.lions.indoor;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
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
    }

//    @Override
//    public void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//
//        canvas.save();
//        canvas.scale(mScaleFactor, mScaleFactor);
//        ...
//        // onDraw() code goes here
//        ...
//        canvas.restore();
//    }

//    @Override
//    public boolean onTouchEvent(MotionEvent ev) {
//        Log.d("scale", "onTouchEvent");
//        // Let the ScaleGestureDetector inspect all events.
//        mScaleDetector.onTouchEvent(ev);
//        return true;
//    }

    public boolean onTouchEvent(MotionEvent event) {
        boolean retVal = mScaleDetector.onTouchEvent(event);
        retVal = mGestureDetector.onTouchEvent(event) || retVal;
        return retVal || super.onTouchEvent(event);
    }


    public void setImage(BitmapDrawable bitmapDrawable) {
        Log.d("scale", "setting image to a bitmapDrawable");
//        imageView = new ImageView(getContext());
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
            Log.d("scale", "onScale - scalefactor: " + mScaleFactor);
            invalidate();
            return true;
        }
    }

    private class PanListener
            extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
//            // Scrolling uses math based on the viewport (as opposed to math using pixels).
//
//            // Pixel offset is the offset in screen pixels, while viewport offset is the
//            // offset within the current viewport.
//            float viewportOffsetX = distanceX * mCurrentViewport.width()
//                    / mContentRect.width();
//            float viewportOffsetY = -distanceY * mCurrentViewport.height()
//                    / mContentRect.height();
//
//            // Updates the viewport, refreshes the display.
//            setViewportBottomLeft(
//                    mCurrentViewport.left + viewportOffsetX,
//                    mCurrentViewport.bottom + viewportOffsetY);

            relativeLayout.setLeft((int) distanceX);
//            relativeLayout.setLeft((int) distanceX);
            Log.d("scale", "onScroll - distanceX: " + distanceX + " distanceY: " + distanceY);
            return true;
        }
    }

//    private final ScaleGestureDetector.OnScaleGestureListener mScaleGestureListener
//            = new ScaleGestureDetector.SimpleOnScaleGestureListener() {
//    private class ScaleListener
//        extends ScaleGestureDetector.SimpleOnScaleGestureListener {
//        /**
//         * This is the active focal point in terms of the viewport. Could be a local
//         * variable but kept here to minimize per-frame allocations.
//         */
//        private PointF viewportFocus = new PointF();
//        private float lastSpanX;
//        private float lastSpanY;
//
//        // Detects that new pointers are going down.
//        @Override
//        public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
//            lastSpanX = scaleGestureDetector.getCurrentSpanX();
//            lastSpanY = scaleGestureDetector.getCurrentSpanY();
//            return true;
//        }
//
//        @Override
//        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
//
//            float spanX = scaleGestureDetector.getCurrentSpanX();
//            float spanY = scaleGestureDetector.getCurrentSpanY();
//
////            float newWidth = lastSpanX / spanX * mCurrentViewport.width();
////            float newHeight = lastSpanY / spanY * mCurrentViewport.height();
//
//            float focusX = scaleGestureDetector.getFocusX();
//            float focusY = scaleGestureDetector.getFocusY();
//            // Makes sure that the chart point is within the chart region.
//            // See the sample for the implementation of hitTest().
////            hitTest(scaleGestureDetector.getFocusX(),
////                    scaleGestureDetector.getFocusY(),
////                    viewportFocus);
////
////            mCurrentViewport.set(
////                    viewportFocus.x
////                            - newWidth * (focusX - mContentRect.left)
////                            / mContentRect.width(),
////                    viewportFocus.y
////                            - newHeight * (mContentRect.bottom - focusY)
////                            / mContentRect.height(),
////                    0,
////                    0);
////            mCurrentViewport.right = mCurrentViewport.left + newWidth;
////            mCurrentViewport.bottom = mCurrentViewport.top + newHeight;
////
////            // Invalidates the View to update the display.
////            ViewCompat.postInvalidateOnAnimation(InteractiveLineGraphView.this);
//
//            lastSpanX = spanX;
//            lastSpanY = spanY;
//
//            relativeLayout.setScaleX(2f);
//            return true;
//        }
//    };
}
