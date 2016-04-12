package com.example.micke.myapplication;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;


/**
 * A placeholder fragment containing a simple view.
 */

public class IndoorListFragment extends Fragment implements DataSetChanged {

    String ILF = "IndoorListFragment";

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    ViewPager mPager;

    //ip stands for interest points.
    private RecyclerView ipRecyclerView;
    private ipAdapter ipadapter;
    private RecyclerView.LayoutManager ipLayoutManager;

    private FireBaseIndoor fireBaseHandler;

    private List<PointOfInterest> myDataset;

    public IndoorListFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static IndoorListFragment newInstance(int sectionNumber) {
        IndoorListFragment fragment = new IndoorListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String buildingId = "1";

        fireBaseHandler = new FireBaseIndoor(getContext(), buildingId);
        fireBaseHandler.test();

        myDataset = fireBaseHandler.getPoints(buildingId, (IndoorActivity) getActivity());

        View rootView = inflater.inflate(R.layout.fragment_indoor_list, container, false);

        ipRecyclerView = (RecyclerView) rootView.findViewById(R.id.ip_recycler_view);
        ipRecyclerView.setHasFixedSize(true);
        ipLayoutManager = new LinearLayoutManager(getActivity());
        ipRecyclerView.setLayoutManager(ipLayoutManager);

        ipadapter = new ipAdapter(myDataset);
        ipRecyclerView.setAdapter(ipadapter);

        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
        ImageButton goToMapImage = (ImageButton) rootView.findViewById(R.id.goToMapImage);
        goToMapImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(ILF, "onClick: start ");

                if (getActivity() == null)
                    Log.d(ILF, "onClick: getActivity = null ");
                mPager = (ViewPager) getActivity().findViewById(R.id.container);
                if (mPager == null)
                    Log.d(ILF, "onClick: mPager = null ");

                mPager.setCurrentItem(0, true);

                Log.d(ILF, "onClick: färdig ");
            }
        });

        return rootView;
    }

    @Override
    public void dataSetChanged() {
        ipadapter.notifyDataSetChanged();
    }
}

    /*Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.d("ItemClicked", "Item: " + item.toString());
        if (id == R.id.item_add) {
            DialogFragment newFragment = new AddPointDialogFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("firebase", fireBaseHandler);
            newFragment.setArguments(bundle);
            newFragment.show(this.getFragmentManager(), "add_point_layout");
        } else if (id == R.id.item_camera) {
            Intent intent = new Intent(getApplicationContext(), QRReader.class);
            startActivity(intent);
        }
        return false; //
    }*/
