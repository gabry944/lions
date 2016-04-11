package com.example.micke.myapplication;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends Fragment {
    //ip stands for interest points.
    private RecyclerView ipRecyclerView;
    private RecyclerView.Adapter ipAdapter;
    private RecyclerView.LayoutManager ipLayoutManager;

    public ListFragment() {
        ipRecyclerView = (RecyclerView) getView().findViewById(R.id.ip_recycler_view);

        // This setting improve performance if changes
        // in content do not change the layout size of the RecyclerView
        ipRecyclerView.setHasFixedSize(true);

        // use a linear layout manager in the Recycler view
        ipLayoutManager = new LinearLayoutManager(getActivity());
        ipRecyclerView.setLayoutManager(ipLayoutManager);

        // temp!!
        PointOfInterest p1 = new PointOfInterest("Toalett", "", "", 0,0,1);
        PointOfInterest p2 = new PointOfInterest("Mötesrum", "Magnum", "", 0,0,1);
        PointOfInterest p3 = new PointOfInterest("Toalett", "", "", 0,0,1);
        PointOfInterest[] myDataset = {p1, p2, p3};

        // specify an adapter
        ipAdapter = new ipAdapter(myDataset);
        ipRecyclerView.setAdapter(ipAdapter);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false);
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
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.mPoint = ipDataset[position];
            holder.mContentView.setText(ipDataset[position].titel);

            holder.mMapIconView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.d("ListActivity", "onClick: start ");

                    Bundle arguments = new Bundle();
                    arguments.putString(IndoorMapFragment.ARG_ITEM_ID, holder.mPoint.getID() + "");
                    arguments.putString(IndoorMapFragment.ARG_ITEM_TITLE, holder.mPoint.titel);
                    arguments.putString(IndoorMapFragment.ARG_ITEM_CATEGORY, holder.mPoint.category );
                    arguments.putString(IndoorMapFragment.ARG_ITEM_DESCRIPTION, holder.mPoint.description);
                    arguments.putString(IndoorMapFragment.ARG_ITEM_LATITUDE, holder.mPoint.latitude + "");
                    arguments.putString(IndoorMapFragment.ARG_ITEM_LONGITUDE, holder.mPoint.longitude + "");
                    arguments.putString(IndoorMapFragment.ARG_ITEM_FLOOR, holder.mPoint.floor + "");

                    IndoorMapFragment fragment = new IndoorMapFragment();
                    fragment.setArguments(arguments);

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.ip_list_layout, fragment)
                            .commit();

                    //fragmentTransaction.replace(R.id.content, fragment);
                    //fragmentTransaction.addToBackStack(null);//add the transaction to the back stack so the user can navigate back
                    //fragmentTransaction.commit();

                    Log.d("ListActivity", "onClick: färdig ");

                }
            });

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return ipDataset.length;
        }
    }

/*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    */
}
