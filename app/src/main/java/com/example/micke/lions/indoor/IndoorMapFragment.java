package com.example.micke.lions.indoor;

import android.app.DialogFragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.micke.lions.Common;
import com.example.micke.lions.InloggChange;
import com.example.micke.lions.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class IndoorMapFragment extends Fragment implements IndoorMapMarkerChange, InloggChange {
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
    private String currentFloor = "3"; //TODO

    private boolean filterMarkers;
    private IndoorMapMarker end = null, entrance = null;
    private List<IndoorMapMarker> floorChange;


    private int displayWidth;
    private int displayHeight;

    private float mx, mx2;  //2 is for the second finger. Used for zooming
    private float my, my2;
    private float scaleFactor = 5.0f;
    private boolean longClick = true;  //turns to false if user moves fingers

    //Stores all ips for the whole building
    private List<PointOfInterest> pointList;

    private IndoorActivity indoorActivity;

    //Stores ips for the current floor as markers
    private List<IndoorMapMarker> listOfMarkers = new ArrayList<IndoorMapMarker>();

    private ImageButton goToList;

    private static final String ARG_SECTION_NUMBER = "section_number";

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
        filterMarkers = false;
        indoorActivity = (IndoorActivity) getActivity();
        fireBaseIndoor = indoorActivity.getFireBaseHandler();

        buildingId = indoorActivity.getBuildingId();
        rootView = inflater.inflate(R.layout.activity_indoor_map, container, false);
        mFloors = fireBaseIndoor.getFloors(buildingId, this);
        floorAdapter = new FloorAdapter(this, mFloors);

        //Get display dimensions
        Display display = indoorActivity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        displayWidth = size.x;
        displayHeight = size.y;

        //For list of floors
        mFloorRecyclerView = (RecyclerView) rootView.findViewById(R.id.floor_recycler_view);
        mFloorRecyclerView.setHasFixedSize(true);
        mFloorLayoutManager = new LinearLayoutManager(indoorActivity);
        mFloorRecyclerView.setLayoutManager(mFloorLayoutManager);

        mFloorRecyclerView.setAdapter(floorAdapter);

        //For the map
        final RelativeLayout r = (RelativeLayout) rootView.findViewById(R.id.mapLayout);
        r.setScaleX(1.0f);
        r.setScaleY(1.0f);

        //Get dimensions of r
        Log.d("point", "getting dimensions...");
        getDimensions(r);

        setHasOptionsMenu(true);

        pointList = fireBaseIndoor.getPoints(buildingId, this);

        goToList = (ImageButton) rootView.findViewById(R.id.goToIndoorList1);
        goToList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewPager mPager = (ViewPager) v.getRootView().findViewById(R.id.container);
                mPager.setCurrentItem(1, true);
            }
        });

        setCurrentFloor("3");

        r.setLongClickable(true);
        r.setClickable(true);
        r.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {

                if(longClick) {
//                    addPoint(r, mx - r.getWidth() / 2,
//                            my - r.getHeight() / 2 + 60);

                    float[] point = new float[2];
                    Log.d("map_pos", "mx = " + mx + " r_width = " + r.getWidth());
                    Log.d("map_pos", "my = " + my + " r_height = " + r.getHeight());
                    point[0] = mx - r.getWidth() / 4;
                    point[1] = my - r.getHeight() / 4 + 60;
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

                            diff = (diff < -ZOOMSPEED) ? -ZOOMSPEED : diff;
                            diff = (diff > ZOOMSPEED) ? ZOOMSPEED : diff;
                            scaleFactor += 0.02 * diff;
                            scaleFactor = (scaleFactor > 10.0f) ? 10.0f : scaleFactor;
                            scaleFactor = (scaleFactor < 1.0f) ? 1.0f : scaleFactor;

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

                            if(deltaX+deltaY > 5)
                                longClick = false;

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

    //hide all except chosen ip and entrance (or stairs/elevator)
    public void highlightIP(String goalFloor, String ipID) {
        setCurrentFloor(goalFloor);
        filterMarkers = true;
        entrance = null;
        end = null;
        floorChange = new ArrayList();

        //Find the goal point
        for(IndoorMapMarker m : listOfMarkers) {
            if(m.getId().equals(ipID))
                end = m;
        }

        //Should never happen
        if(end == null) return;

        if(!end.getCategory().equals(getString(R.string.Elevator)) && !end.getCategory().equals(getString(R.string.Stairs))
                && !end.getCategory().equals(getString(R.string.Entrance))) {
            //Find the closest entrance (if it exist on this floor)
            float distance = Float.MAX_VALUE;
            for (IndoorMapMarker m : listOfMarkers) {
                if (m.getCategory().equals(getString(R.string.Entrance)) && distance > calcDistance(m.getX(), m.getY(), end.getX(), end.getY())) {
                    entrance = m;
                    distance = calcDistance(m.getX(), m.getY(), end.getX(), end.getY());
                }
            }

            //If we didn't find an entrance we look for the closest elevator or stairs instead
            if (entrance == null) {
                for (IndoorMapMarker m : listOfMarkers) {
                    if ((m.getCategory().equals(getString(R.string.Elevator)) || m.getCategory().equals(getString(R.string.Stairs)))
                            && distance > calcDistance(m.getX(), m.getY(), end.getX(), end.getY())) {
                        entrance = m;
                        distance = calcDistance(m.getX(), m.getY(), end.getX(), end.getY());
                    }
                }
            }
        } else entrance = end;

        for (IndoorMapMarker m : listOfMarkers) {
            m.getMarker().setVisibility(View.GONE);
        }

        end.getMarker().setVisibility(View.VISIBLE);

        //Return if we didn't find an elevator/stairs or entrance
        if(entrance == null) {
            Toast toast = Toast.makeText(getContext(), "Couldn't find an entrance, elevator or stairs!", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        entrance.getMarker().setVisibility(View.VISIBLE);
    }

    //Calculates the distance between two points
    private float calcDistance(float point1X, float point1Y, float point2X, float point2Y) {
        return (float) Math.sqrt( Math.pow((point1X-point2X), 2) + Math.pow((point1Y-point2Y), 2) );
    }

    private void addPoint(RelativeLayout parent, PointOfInterest ip) {
        //TODO fixa vettiga värden
        final float posX = ip.getLatitude();
        final float posY = ip.getLongitude();

        final IndoorMapMarker point = new IndoorMapMarker(ip, posX, posY, getContext());

        parent.addView(point.getMarker());

        listOfMarkers.add(point);

        if(ip.getCategory().toLowerCase().equals("hiss"))
            addDescText(parent, ip.getCategory(), point.getX(), point.getY());

        point.getMarker().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (point.getOfficial() && !Common.IsAdmin())
                {
                    Toast toast = Toast.makeText(getContext(),R.string.adminRequierdForChangingPoint, Toast.LENGTH_LONG);
                    toast.show();
                }
                else
                {
                    ChangePointDialogFragment ask = new ChangePointDialogFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("id",point.getId());
                    ask.setArguments(bundle);
                    ask.show(indoorActivity.getFragmentManager(),"remove_point_fragment");
                }
            }
        });
    }

    public void RemovePoint(String pointId)
    {
        IndoorMapMarker point = null;
        for (IndoorMapMarker p : listOfMarkers){
            if(p.getId().equals(pointId)){
                point = p;
                break;
            }
        }
        if (point!=null)
        {
            //remove from map
            listOfMarkers.remove(point);
            pointList.remove(point.getPoint());
            RelativeLayout r = (RelativeLayout) rootView.findViewById(R.id.mapLayout);
            r.removeView(point.getMarker());

            //remove from fierbase
            fireBaseIndoor.removeIp(point.getPoint());
            Log.d(TAG, "Punkt ska vara borta ");
        }
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
        int id = item.getItemId();
        if (id == R.id.floors) {
            if(getActivity().findViewById(R.id.floor_recycler_view).getVisibility() == View.GONE)
                getActivity().findViewById(R.id.floor_recycler_view).setVisibility(View.VISIBLE);
            else
                getActivity().findViewById(R.id.floor_recycler_view).setVisibility(View.GONE);
        }
        else if (id == R.id.addInterestPoint) {
            Context context = getContext();
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, R.string.addMarkerExplanation, duration);
            toast.show();
            toast.setGravity(Gravity.TOP| Gravity.CENTER, 0, 150);

        }
        else if(id == R.id.restorePoints) {
            setCurrentFloor(fireBaseIndoor.getFloor());
            filterMarkers = false;
        }
        return false;
    }

    public void setCurrentFloor(String floor) {
        ImageView i = (ImageView) rootView.findViewById(R.id.map);
        fireBaseIndoor.setFloor(floor);
        floorMap = null;
        currentFloor = floor;
        if (floor.equals("3")) {
            //floorMap = getResources().getDrawable(R.drawable.map_t3);
            floorMap = new BitmapDrawable(getResources(), getFloorImage(R.drawable.map_t3));
        }
        if (floor.equals("4")) {
            //floorMap = getResources().getDrawable(R.drawable.map_t4);
            floorMap = new BitmapDrawable(getResources(), getFloorImage(R.drawable.map_t4));
        }
        i.setImageDrawable(floorMap);

        RelativeLayout r = (RelativeLayout) rootView.findViewById(R.id.mapLayout);
        for (IndoorMapMarker p : listOfMarkers)
            r.removeView(p.getMarker());
        listOfMarkers.clear();
        for (PointOfInterest p : pointList) {
            if (p.getFloor().equals(floor) || p.getCategory().equals(getString(R.string.Stairs))
                    || p.getCategory().equals(getString(R.string.Elevator))) {
                addPoint(r, p);
            }
        }
    }

    @Override
    public void getUpdatedDataSet(List<PointOfInterest> pointList) {
        RelativeLayout r = (RelativeLayout) rootView.findViewById(R.id.mapLayout);

        for(IndoorMapMarker p : listOfMarkers) {
            r.removeView(p.getMarker());
        }
        listOfMarkers.clear();
        for(PointOfInterest p : pointList) {
            if(p.getFloor().equals(currentFloor) || p.getCategory().equals(getString(R.string.Stairs))
                    || p.getCategory().equals(getString(R.string.Elevator)))
                addPoint(r,p);
        }
        floorAdapter.notifyDataSetChanged();
    }

    @Override
    public void dataSetChanged() {
        floorAdapter.notifyDataSetChanged();
    }

    //Get dimension
    private void getDimensions(final RelativeLayout relativeLayout) {
        ViewTreeObserver vto = relativeLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ViewTreeObserver obs = relativeLayout.getViewTreeObserver();

                obs.removeOnGlobalLayoutListener(this);
                final Point point = new Point(relativeLayout.getWidth(), relativeLayout.getHeight());
                Log.d("point", "x " + point.x + " y " + point.y);
            }
        });
    }

    //Load bitmaps efficiently
    private Bitmap getFloorImage(int resId) {
        Log.d("display", "w: " + displayWidth + " h: " + displayHeight);
        return decodeSampledBitmapFromResource(getResources(), resId, displayWidth, displayHeight);
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        //temp width & height
        reqHeight = 256;
        reqWidth = 256;
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

    @Override
    public void adminInlogg() {
        Log.d(TAG, "adminInlogg: ");
    }

    @Override
    public void commonInlogg() {
        Log.d(TAG, "commonInlogg: ");
    }

    public IndoorMapMarker getEnd() {
        return end;
    }

    public boolean getFilterMarkers() {
        return filterMarkers;
    }


}

