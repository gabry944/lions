package com.example.micke.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SeekBar;

import java.util.ArrayList;
import java.util.List;
/*
public class Map extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map2);
    }
}
*/

public class Map extends AppCompatActivity {

    float mx;
    float my;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map2);

        final ImageView switcherView = (ImageView) this.findViewById(R.id.map);

        switcherView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View arg0, MotionEvent event) {

                float curX, curY;

                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        mx = event.getX();
                        my = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        curX = event.getX();
                        curY = event.getY();
                        Log.d("hej", "X = " + Float.toString(event.getX()) + " Y = " + Float.toString(event.getY()));
                        switcherView.scrollBy((int) (mx - curX), (int) (my - curY));
                        mx = curX;
                        my = curY;
                        break;
                    case MotionEvent.ACTION_UP:
                        curX = event.getX();
                        curY = event.getY();
                        switcherView.scrollBy((int) (mx - curX), (int) (my - curY));
                        break;
                }

                return true;
            }
        });

    }
}

/*

// set maximum scroll amount (based on center of image)
    int maxX = (int)((bitmapWidth / 2) - (screenWidth / 2));
    int maxY = (int)((bitmapHeight / 2) - (screenHeight / 2));

    // set scroll limits
    final int maxLeft = (maxX * -1);
    final int maxRight = maxX;
    final int maxTop = (maxY * -1);
    final int maxBottom = maxY;

    // set touchlistener
    ImageView_BitmapView.setOnTouchListener(new View.OnTouchListener()
    {
        float downX, downY;
        int totalX, totalY;
        int scrollByX, scrollByY;
        public boolean onTouch(View view, MotionEvent event)
        {
            float currentX, currentY;
            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    downX = event.getX();
                    downY = event.getY();
                    break;

                case MotionEvent.ACTION_MOVE:
                    currentX = event.getX();
                    currentY = event.getY();
                    scrollByX = (int)(downX - currentX);
                    scrollByY = (int)(downY - currentY);

                    // scrolling to left side of image (pic moving to the right)
                    if (currentX > downX)
                    {
                        if (totalX == maxLeft)
                        {
                            scrollByX = 0;
                        }
                        if (totalX > maxLeft)
                        {
                            totalX = totalX + scrollByX;
                        }
                        if (totalX < maxLeft)
                        {
                            scrollByX = maxLeft - (totalX - scrollByX);
                            totalX = maxLeft;
                        }
                    }

                    // scrolling to right side of image (pic moving to the left)
                    if (currentX < downX)
                    {
                        if (totalX == maxRight)
                        {
                            scrollByX = 0;
                        }
                        if (totalX < maxRight)
                        {
                            totalX = totalX + scrollByX;
                        }
                        if (totalX > maxRight)
                        {
                            scrollByX = maxRight - (totalX - scrollByX);
                            totalX = maxRight;
                        }
                    }

                    // scrolling to top of image (pic moving to the bottom)
                    if (currentY > downY)
                    {
                        if (totalY == maxTop)
                        {
                            scrollByY = 0;
                        }
                        if (totalY > maxTop)
                        {
                            totalY = totalY + scrollByY;
                        }
                        if (totalY < maxTop)
                        {
                            scrollByY = maxTop - (totalY - scrollByY);
                            totalY = maxTop;
                        }
                    }

                    // scrolling to bottom of image (pic moving to the top)
                    if (currentY < downY)
                    {
                        if (totalY == maxBottom)
                        {
                            scrollByY = 0;
                        }
                        if (totalY < maxBottom)
                        {
                            totalY = totalY + scrollByY;
                        }
                        if (totalY > maxBottom)
                        {
                            scrollByY = maxBottom - (totalY - scrollByY);
                            totalY = maxBottom;
                        }
                    }

                    ImageView_BitmapView.scrollBy(scrollByX, scrollByY);
                    downX = currentX;
                    downY = currentY;
                    break;

            }

            return true;
        }
    });
I'm sure it could be refined a bit, but it works pretty well. :)

shareimprove this answer
 */