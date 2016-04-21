package com.example.micke.lions.indoor;

import android.app.DialogFragment;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.micke.lions.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * A placeholder fragment containing a simple view.
 */
public class IndoorMapFragment extends Fragment implements IndoorMapMarkerChange {
    String TAG = "IndoorMapFragment";
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private RecyclerView mFloorRecyclerView;
    private RecyclerView.LayoutManager mFloorLayoutManager;

    private View rootView;
    private List<String> mFloors;
    public FloorAdapter floorAdapter;
    private FireBaseIndoor fireBaseIndoor;
    private String buildingId;
    private int currentFloor;

    private float mx, mx2;  //2 is for the second finger. Used for zooming
    private float my, my2;
    private float scaleFactor = 5.0f;
    private boolean longClick = true;  //turns to false if user moves fingers

    private List<PointOfInterest> pointList;

    private IndoorActivity indoorActivity;
    private List<IndoormapMarker> listOfMarkers = new ArrayList<IndoormapMarker>();

    private static final String ARG_SECTION_NUMBER = "section_number";

    //TODO Change to list
    private Drawable floorMap;

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

        indoorActivity = (IndoorActivity) getActivity();
        fireBaseIndoor = indoorActivity.getFireBaseHandler();

        buildingId = indoorActivity.getBuildingId();
        rootView = inflater.inflate(R.layout.activity_indoor_map, container, false);
        mFloors = fireBaseIndoor.getFloors(buildingId, this);
        floorAdapter = new FloorAdapter(this, mFloors);

        //For list of floors
        mFloorRecyclerView = (RecyclerView) rootView.findViewById(R.id.floor_recycler_view);
        mFloorRecyclerView.setHasFixedSize(true);
        mFloorLayoutManager = new LinearLayoutManager(indoorActivity);
        mFloorRecyclerView.setLayoutManager(mFloorLayoutManager);

        mFloorRecyclerView.setAdapter(floorAdapter);

        //For the map
        final RelativeLayout r = (RelativeLayout) rootView.findViewById(R.id.mapLayout);
        r.setScaleX(5.0f);
        r.setScaleY(5.0f);


        floorMap = getResources().getDrawable(R.drawable.map_t3);
        final ImageView i = (ImageView) rootView.findViewById(R.id.map);
        i.setImageDrawable(floorMap);

        setHasOptionsMenu(true);

        pointList = fireBaseIndoor.getPoints(buildingId, this);

        TextView textView = new TextView(getContext());
        textView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        textView.setText("Hej");

        r.addView(textView);

        textView.setX(0);
        textView.setY(0);

        r.setLongClickable(true);
        r.setClickable(true);
        r.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {

                if(longClick) {
//                    addPoint(r, mx - r.getWidth() / 2,
//                            my - r.getHeight() / 2 + 60);

                    float[] point = new float[2];
                    point[0] = mx - r.getWidth() / 2;
                    point[1] = my - r.getHeight() / 2 + 60;
                    DialogFragment newFragment = new AddPointDialogFragment();
                    Bundle bundle = new Bundle();
                    bundle.putFloat("lat", point[0]);
                    bundle.putFloat("lng", point[1]);
                    newFragment.setArguments(bundle);
                    newFragment.show(indoorActivity.getFragmentManager(), "add_point_layout");
                }
                        Log.d("map_indoor", "onLongClick: " + longClick);
                return false;
            }
        });

        r.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View arg0, MotionEvent event) {

                float posX, posY;
                float curX, curY;

                final float SCROLLSPEED = 30.0f;
                final float ZOOMSPEED = 50.0f;

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
                            longClick = false;
                            float zoomVectorX = mx - mx2;
                            float zoomVectorY = my - my2;
                            float newZoomVectorX = event.getX(0) - event.getX(1);
                            float newZoomVectorY = event.getY(0) - event.getY(1);

                            //diff = distance between finger motion based on percentage difference
                            double diff = (double) scaleFactor * 20 *
                                    ((Math.sqrt(Math.pow(newZoomVectorX, 2.0) + Math.pow(newZoomVectorY, 2.0)) -
                                            Math.sqrt(Math.pow(zoomVectorX, 2.0) + Math.pow(zoomVectorY, 2.0)))
                                            / Math.sqrt(Math.pow(zoomVectorX, 2.0) + Math.pow(zoomVectorY, 2.0)));

                            //if (diff != 0)
                            //    diff = 25* Math.signum(diff) * 1 / Math.pow(diff, 2.0);
                            diff = (diff < -ZOOMSPEED) ? -ZOOMSPEED : diff;
                            diff = (diff > ZOOMSPEED) ? ZOOMSPEED : diff;
                            scaleFactor += 0.02 * diff;
                            scaleFactor = (scaleFactor > 10.0f) ? 10.0f : scaleFactor;
                            scaleFactor = (scaleFactor < 1.0f) ? 1.0f : scaleFactor;

                            //Log.d("map_indoor", "Two fingers: scaleFactor = " + scaleFactor + ", diff = " + diff);
                            r.setScaleX(scaleFactor);
                            r.setScaleY(scaleFactor);
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

                            float deltaX = mx - curX;
                            float deltaY = my - curY;

                            //Log.d("map_indoor", "posX = " + event.getRawX() + ", posY = " + event.getRawY());
                            //Log.d("map_indoor", "deltaX = " + deltaX + ", deltaY = " + deltaY);

                            if(deltaX+deltaY > 0.1)
                                longClick = false;
                            //This coordinate transformation should be moved into its own function
                            //addPoint(r, (event.getRawX() - r.getWidth() / 2 - r.getTranslationX())/scaleFactor,
                            // (event.getRawY() - r.getHeight() / 2 - 100 - r.getTranslationY())/scaleFactor);

                            r.setTranslationX(posX - deltaX);
                            r.setTranslationY(posY - deltaY);


                            mx = curX;
                            my = curY;
                        }

                        break;

                    case MotionEvent.ACTION_UP:
                        longClick = true;
                        break;

                }

                return false;
            }
        });

        return rootView;
    }

    public void highlightIP(String ipID) {
        Log.d(TAG, "highlightIP: piID = " + ipID);
        for(IndoormapMarker m : listOfMarkers) {
            //hide all except chosen ip and entrance
            if(m.getId().equals(ipID) || m.getCategory().equals("Entrance"))
                m.getMarker().setVisibility(View.VISIBLE);
            else
                m.getMarker().setVisibility(View.GONE);
        }
    }

    private void addPoint(RelativeLayout parent, PointOfInterest ip) {
        //TODO fixa vettiga värden
        final float posX = ip.getLatitude();
        final float posY = ip.getLongitude();

        IndoormapMarker point = new IndoormapMarker(ip, posX, posY, getContext());
        parent.addView(point.getMarker());
        listOfMarkers.add(point);

        if(ip.getCategory().toLowerCase().equals("hiss"))
                    addDescText(parent, ip.getCategory(), point.getX(), point.getY());

        point.getMarker().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "Klickar på pungtjävel " + "posX = " + posX + " posY = " + posY);
            }
        });
    }

    private void addDescText(RelativeLayout parent, String category, float posX, float posY){
        TextView textView = new TextView(getContext());
        textView.setText(category);
        textView.setTextSize(6);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        textView.setLayoutParams(layoutParams);
        textView.setX(posX);
        textView.setY(posY - 130);
        parent.addView(textView);
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_indoor_map, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        // return super.onOptionsItemSelected(item);

        int id = item.getItemId();
        if (id == R.id.floors) {
            if(getActivity().findViewById(R.id.floor_recycler_view).getVisibility() == View.GONE)
                getActivity().findViewById(R.id.floor_recycler_view).setVisibility(View.VISIBLE);
            else
                getActivity().findViewById(R.id.floor_recycler_view).setVisibility(View.GONE);
        }
        return false;
    }

    public void setCurrentFloor(String floor) {
        ImageView i = (ImageView) rootView.findViewById(R.id.map);
        int id = 0;
        Log.d("floor", "" + floor);
        fireBaseIndoor.setFloor(floor);
        floorMap = null;
        if(floor.equals("3")) {
            currentFloor = 3;
            floorMap = getResources().getDrawable(R.drawable.map_t3);
        }
        if(floor.equals("4")) {
            currentFloor = 4;
            floorMap = getResources().getDrawable(R.drawable.map_t4);
        }
        i.setImageDrawable(floorMap);
    }

    @Override
    public void getUpdatedDataSet(List<PointOfInterest> pointList) {
        RelativeLayout r = (RelativeLayout) rootView.findViewById(R.id.mapLayout);
        Log.d("floor", "pointList size = " + pointList.size());
        for(IndoormapMarker p : listOfMarkers) {
            r.removeView(p.getMarker());
        }
        listOfMarkers.clear();
        for(PointOfInterest p : pointList) {
            addPoint(r,p);
        }
        floorAdapter.notifyDataSetChanged();
    }

    @Override
    public void dataSetChanged() {
        floorAdapter.notifyDataSetChanged();
    }

    //Load bitmaps efficiently
    private Bitmap getFloorImage(int resId) {
        final RelativeLayout r = (RelativeLayout) rootView.findViewById(R.id.mapLayout);
        int displayWidth = r.getWidth();
        int displayHeight = r.getHeight();
        return decodeSampledBitmapFromResource(getResources(), resId, displayWidth, displayHeight);
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}

