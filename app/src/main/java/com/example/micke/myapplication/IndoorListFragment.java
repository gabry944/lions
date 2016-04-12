package com.example.micke.myapplication;

import android.app.ListFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class IndoorListFragment extends Fragment {
    String ILF = "IndoorListFragment";
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    ViewPager mPager;

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
        View rootView = inflater.inflate(R.layout.fragment_indoor_list, container, false);
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
                if(mPager == null)
                    Log.d(ILF, "onClick: mPager = null ");

                mPager.setCurrentItem(2, true);

                Log.d(ILF, "onClick: färdig ");
            }
        });
        return rootView;
    }

    class ipAdapter extends RecyclerView.Adapter<ipAdapter.ViewHolder> {
        private PointOfInterest[] ipDataset;
        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public final View mView;
            public final TextView mContentView;
            public final ImageView mMapIconView;
            public PointOfInterest mPoint;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mContentView = (TextView) view.findViewById(R.id.title);
                mMapIconView = (ImageView) view.findViewById(R.id.goToMapImage);
            }
        }
        // Provide a suitable constructor (depends on the kind of dataset)
        public ipAdapter(PointOfInterest[] myDataset) {
            ipDataset = myDataset;
        }
        // Create new views (invoked by the layout manager)
        @Override
        public ipAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.ip_list_view, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }
        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            Log.d(ILF, "onBindViewHolder: start");
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.mPoint = new PointOfInterest("Test", "", "", 0, 0, "");
            holder.mContentView.setText("titel");
            holder.mMapIconView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(ILF, "onClick: start ");
                    Bundle arguments = new Bundle();
                    //arguments.putString(IndoorMapFragment.ARG_ITEM_ID, holder.mPoint.getID() + "");
                    //arguments.putString(IndoorMapFragment.ARG_ITEM_TITLE, holder.mPoint.titel);
                    arguments.putString(IndoorMapFragment.ARG_ITEM_CATEGORY, holder.mPoint.category);
                    arguments.putString(IndoorMapFragment.ARG_ITEM_DESCRIPTION, holder.mPoint.description);
                    arguments.putString(IndoorMapFragment.ARG_ITEM_LATITUDE, holder.mPoint.latitude + "");
                    arguments.putString(IndoorMapFragment.ARG_ITEM_LONGITUDE, holder.mPoint.longitude + "");
                    //arguments.putString(IndoorMapFragment.ARG_ITEM_FLOOR, holder.mPoint.floor + "");
                    IndoorMapFragment fragment = new IndoorMapFragment();
                    fragment.setArguments(arguments);
                    /*getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_layout, fragment)
                            .commit();*/
                    //fragmentTransaction.replace(R.id.content, fragment);
                    //fragmentTransaction.addToBackStack(null);//add the transaction to the back stack so the user can navigate back
                    //fragmentTransaction.commit();

                    if (getActivity() == null)
                        Log.d(ILF, "onClick: getActivity = null ");
                    mPager = (ViewPager) getActivity().findViewById(R.id.container);
                    if(mPager == null)
                        Log.d(ILF, "onClick: mPager = null ");

                    mPager.setCurrentItem(2, true);

                    Log.d(ILF, "onClick: färdig ");
                }
            });
        }
        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return ipDataset.length;
        }
    }

}
