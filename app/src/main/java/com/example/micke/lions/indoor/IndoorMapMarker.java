package com.example.micke.lions.indoor;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.micke.lions.R;


public class IndoorMapMarker {
    String TAG = "IndoorMapMarker";

    private ImageView point;
    private PointOfInterest pointOfInterest;
    private final Context context;
    private float[] localCoord;    //[0] = posX, [1] = posY
    MapImage mapImage;

    //Used when user want to move a point
    private boolean moving = false;

    public IndoorMapMarker(final PointOfInterest pointOfInterest, float posX, float posY, final Context context) {
        this.context = context;
        this.pointOfInterest = pointOfInterest;
        mapImage = (MapImage) ((IndoorActivity)context).findViewById(R.id.scale_test);
        localCoord = new float[2];

        localCoord[0] = posX;
        localCoord[1] = posY;
        //TODO replace with function to calaulate x and y from long and lat.
        //localCoord = transformCoordToLocal(pointOfInterest.getGlobalCoord());

        setUpImageView();

        point.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent e) {
                int action = e.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        Log.d("hejhej", "touching");
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //Runs when user changes position of a point
                        if(moving) {
                            float[] point = new float[2];
                            point[0] = localCoord[0] + e.getX();
                            point[1] = localCoord[1] + e.getY();
                            setX(point[0]);
                            setY(point[1]);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if(moving) {
                            moving = false;

                            Log.d("hejhej", getX() + " : " + getY());
                            float[] pos = mapImage.convertCoordinatesPercent(getX(), getY());

                            PointOfInterest point = new PointOfInterest(
                                    pointOfInterest.getTitle(),
                                    pointOfInterest.getDescription(),
                                    pointOfInterest.getCategory(),
                                    pos[0],
                                    pos[1],
                                    ((IndoorActivity)context).getFireBaseHandler().getFloor(),
                                    pointOfInterest.getOfficial(),
                                    pointOfInterest.getId()
                            );
                            ((IndoorActivity)context).getFireBaseHandler().updateIp(point,
                                    Integer.parseInt(((IndoorActivity)context).getFireBaseHandler().getFloor()));
                            final Toast toast = Toast.makeText(context, "Punkt flyttad!", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        break;
                    default:
                        break;
                }

                //Override other touch listeners if we are moving the point
                return moving;
            }
        });
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

    public String getTitle(){
        return pointOfInterest.getTitle();
    }

    public String getDescription(){
        return pointOfInterest.getDescription();
    }

    public boolean getOfficial() { return pointOfInterest.getOfficial(); }

    public void setMoving(boolean val) {
        moving = val;
    }

    public void setX(float x){
        localCoord[0] = x;
        point.setX(x);
        //pointOfInterest.setLatitude(transformCoordToGlobalLatitude(localCoord));
        //pointOfInterest.setLongitude(transformCoordToGlobalLongitude(localCoord));
        //TODO Update IP in fierbase
    }

    public void setY(float y) {
        localCoord[1] = y;
        point.setY(y);
        //pointOfInterest.setLatitude(transformCoordToGlobalLatitude(localCoord));
        //pointOfInterest.setLongitude(transformCoordToGlobalLongitude(localCoord));
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
        point.setAdjustViewBounds(true);
        //change so that it will be in the middle of the icon instead of top left corner
        point.setX(localCoord[0]-20);
        point.setY(localCoord[1]-20);
        point.setMaxHeight(40);
        point.setMaxWidth(40);
        point.setMinimumHeight(40);
        point.setMinimumWidth(40);

        if(getCategory().equals(context.getString(R.string.Entrance)))
            point.setImageResource( getOfficial() ? R.drawable.entrance_green : R.drawable.entrance_new );
        else if(getCategory().equals(context.getString(R.string.Elevator)))
            point.setImageResource( getOfficial() ? R.drawable.elevator_marker_green : R.drawable.elevator_new );
        else if (getCategory().equals(context.getString(R.string.Stairs)))
            point.setImageResource( getOfficial() ? R.drawable.stairs_green : R.drawable.stairs_menu );
        else if (getCategory().equals(context.getString(R.string.Toilet)))
            point.setImageResource( getOfficial() ? R.drawable.wc_green : R.drawable.wc);
        else
            point.setImageResource( getOfficial() ? R.drawable.map_marker_green : R.drawable.navigation);
    }
}
