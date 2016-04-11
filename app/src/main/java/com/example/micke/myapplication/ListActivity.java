package com.example.micke.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ListActivity extends AppCompatActivity {
    //ip stands for interest points.
    private RecyclerView ipRecyclerView;
    private RecyclerView.Adapter ipAdapter;
    private RecyclerView.LayoutManager ipLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ipRecyclerView = (RecyclerView) findViewById(R.id.ip_recycler_view);

        // This setting improve performance if changes
        // in content do not change the layout size of the RecyclerView
        ipRecyclerView.setHasFixedSize(true);

        // use a linear layout manager in the Recycler view
        ipLayoutManager = new LinearLayoutManager(this);
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

                //getSupportFragmentManager().beginTransaction()
                //        .add(R.id.item_detail_container, fragment)
                //        .commit();
            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return ipDataset.length;
    }
}
