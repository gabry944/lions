package com.example.micke.lions.indoor;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by iSirux on 2016-04-21.
 */
public class ScaleTest extends View {

    private float mScaleFactor = 1.f;
    private ScaleGestureDetector mScaleDetector;
    private RelativeLayout relativeLayout;
    private ImageView imageView;

    public ScaleTest(Context context, RelativeLayout relativeLayout) {
        super(context);
        this.relativeLayout = relativeLayout;
        mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
        imageView = new ImageView(context);
    }

    public ScaleTest(Context context, AttributeSet attrs) {
        super(context, attrs);
        imageView = new ImageView(context);
        Log.d("scale", "constructed with context/attrs");
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Log.d("scale", "onTouchEvent");
        // Let the ScaleGestureDetector inspect all events.
        mScaleDetector.onTouchEvent(ev);
        return true;
    }

    public void setImage(BitmapDrawable bitmapDrawable) {
        Log.d("scale", "setting image to a bitmapDrawable");
        imageView.setImageDrawable(bitmapDrawable);
    }

    private class ScaleListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            Log.d("scale", "onScale");
            mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));

//            invalidate();
            return true;
        }
    }
}
