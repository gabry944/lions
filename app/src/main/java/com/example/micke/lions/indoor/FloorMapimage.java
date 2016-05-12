package com.example.micke.lions.indoor;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 12/05/2016.
 */
public class FloorMapimage {
    public Bitmap mapimage;
    public int floor;

    public FloorMapimage(Bitmap left, int right) {
        this.mapimage = left;
        this.floor = right;
    }
}