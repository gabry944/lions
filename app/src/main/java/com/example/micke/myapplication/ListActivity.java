package com.example.micke.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ListActivity extends AppCompatActivity {
    //ip stands for interest points.
    private RecyclerView ipRecyclerView;
    private RecyclerView.Adapter ipAdapter;
    private RecyclerView.LayoutManager ipLayoutManager;

    private FireBaseHandler fireBaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fireBaseHandler = new FireBaseHandler(getApplicationContext());
        fireBaseHandler.test();

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
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
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
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mIdView.setText(String.format("%d", ipDataset[position].getID()));
        holder.mContentView.setText(ipDataset[position].titel);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return ipDataset.length;
    }
}
