package com.example.micke.lions.indoor;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.example.micke.lions.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class IndoorMapFragment extends Fragment {
    String TAG = "IndoorMapFragment";
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private RecyclerView mFloorRecyclerView;
    private RecyclerView.LayoutManager mFloorLayoutManager;

    private float mx, mx2;  //2 is for the second finger. Used for zooming
    private float my, my2;
    private float scaleFactor = 5.0f;
    private boolean longClick = true;  //turns to false if user moves fingers

    private static final String ARG_SECTION_NUMBER = "section_number";

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

        View rootView = inflater.inflate(R.layout.activity_indoor_map, container, false);

        //For list of floors
        mFloorRecyclerView = (RecyclerView) rootView.findViewById(R.id.floor_recycler_view);
        mFloorRecyclerView.setHasFixedSize(true);
        mFloorLayoutManager = new LinearLayoutManager(getActivity());
        mFloorRecyclerView.setLayoutManager(mFloorLayoutManager);

        mFloorRecyclerView.setAdapter(((IndoorActivity) getActivity()).floorAdapter);

        //For the map
        final RelativeLayout r = (RelativeLayout) rootView.findViewById(R.id.mapLayout);
        r.setScaleX(5.0f);
        r.setScaleY(5.0f);

        final ImageView i = (ImageView) rootView.findViewById(R.id.map);
        i.setImageResource(R.drawable.map_t3);

        setHasOptionsMenu(true);

        //List<PointOfInterest> l = ((IndoorActivity) getActivity()).getData();

        r.setLongClickable(true);
        r.setClickable(true);
        r.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {

                if(longClick) {
                    addPoint(r, mx - r.getWidth() / 2,
                            my - r.getHeight() / 2 + 60);

                    DialogFragment newFragment = new AddPointDialogFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("firebase", ((IndoorActivity) getActivity()).getFireBaseHandler());
                    newFragment.setArguments(bundle);
                    newFragment.show(getActivity().getFragmentManager(), "add_point_layout");
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

                            Log.d("map_indoor", "Two fingers: scaleFactor = " + scaleFactor + ", diff = " + diff);
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

                            Log.d("map_indoor", "posX = " + event.getRawX() + ", posY = " + event.getRawY());
                            Log.d("map_indoor", "deltaX = " + deltaX + ", deltaY = " + deltaY);

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
        Log.d("IndoorMapFragment", "highlightIP: ipID = " + ipID);
    }

    private void addPoint(RelativeLayout parent, final float posX, final float posY) {

        PointOfInterest dummyPoint = new PointOfInterest("dummyTitle", "dummyDescription", "dummyCategory", 0, 0, "dummyId");
        IndoormapMarker point = new IndoormapMarker(dummyPoint, posX, posY, getContext());
        parent.addView(point.getMarker());

        point.getMarker().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "Klickar på pungtjävel " + "posX = " + posX + " posY = " + posY);
            }
        });
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
}
