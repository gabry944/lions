package com.example.micke.lions.indoor;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.ImageView;

import com.example.micke.lions.R;


public class IndoorMapMarker {
    String TAG = "IndoorMapMarker";

    private ImageView point;
    private PointOfInterest pointOfInterest;
    private Context context;
    private float[] localCoord;    //[0] = posX, [1] = posY

    public IndoorMapMarker(PointOfInterest pointOfInterest, float posX, float posY, Context context) {
        this.context = context;
        this.pointOfInterest = pointOfInterest;

        localCoord = new float[2];

        localCoord[0] = posX;
        localCoord[1] = posY;
        //TODO replace with function to calaulate x and y from long and lat.
        //localCoord = transformCoordToLocal(pointOfInterest.getGlobalCoord());

        setUpImageView();
    }

    //! return the imageView that represent the marker
    public ImageView getMarker() {
        return point;
    }

    public PointOfInterest getPoint(){
        return pointOfInterest;
    }

    public float getX(){
        return localCoord[0];
    }

    public float getY(){
        return localCoord[1];
    }

    public String getId(){
        return pointOfInterest.getId();
    }

    public String getCategory(){ return pointOfInterest.getCategory(); }

    public void setX(float x){
        localCoord[0] = x;
        point.setX(x);
        pointOfInterest.setLatitude(transformCoordToGlobalLatitude(localCoord));
        pointOfInterest.setLongitude(transformCoordToGlobalLongitude(localCoord));
        //TODO Update IP in fierbase
    }

    public void setY(float y) {
        localCoord[1] = y;
        point.setY(y);
        pointOfInterest.setLatitude(transformCoordToGlobalLatitude(localCoord));
        pointOfInterest.setLongitude(transformCoordToGlobalLongitude(localCoord));
        //TODO Update IP in fierbase
    }

    public float[] transformCoordToLocal(float[] globalCoord){
        return globalCoord;
    }

    public float transformCoordToGlobalLatitude(float[] localCoord){
        return localCoord[0];
    }

    public float transformCoordToGlobalLongitude(float[] localCoord){
        return localCoord[1];
    }

    private void setUpImageView(){
        point = new ImageView(context);
        point.setX(localCoord[0]);
        point.setY(localCoord[1]);
        point.setScaleX(0.05f);
        point.setScaleY(0.05f);
        if(getCategory().equals(context.getString(R.string.Entrance)))
            point.setImageResource(R.drawable.entrance);
        else if(getCategory().equals(context.getString(R.string.Elevator)))
            point.setImageResource(R.drawable.elevator_marker);
        else if (getCategory().equals(context.getString(R.string.Stairs)))
            point.setImageResource(R.drawable.stairs);
        else if (getCategory().equals(context.getString(R.string.Toilet)))
            point.setImageResource(R.drawable.wc);
        else
            point.setImageResource(R.drawable.map_marker);
    }
}
