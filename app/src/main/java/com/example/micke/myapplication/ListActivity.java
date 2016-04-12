package com.example.micke.myapplication;


import android.app.DialogFragment;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.TextView;

import java.util.List;

public class ListActivity extends AppCompatActivity implements DataSetChanged {
    //ip stands for interest points.
    private RecyclerView ipRecyclerView;
    private RecyclerView.Adapter ipAdapter;
    private RecyclerView.LayoutManager ipLayoutManager;

    private FireBaseIndoor fireBaseHandler;

    private List<PointOfInterest> myDataset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String buildingId = "1";

        fireBaseHandler = new FireBaseIndoor(getApplicationContext(), buildingId);
        fireBaseHandler.test();

        ipRecyclerView = (RecyclerView) findViewById(R.id.ip_recycler_view);

        // This setting improve performance if changes
        // in content do not change the layout size of the RecyclerView
        ipRecyclerView.setHasFixedSize(true);

        // use a linear layout manager in the Recycler view
        ipLayoutManager = new LinearLayoutManager(this);
        ipRecyclerView.setLayoutManager(ipLayoutManager);

//        myDataset = fireBaseHandler.getPoints(buildingId, this);

        // specify an adapter
//        ipAdapter = new ipAdapter(myDataset);
//        ipRecyclerView.setAdapter(ipAdapter);
    }

    @Override
    public void dataSetChanged() {
        ipAdapter.notifyDataSetChanged();
    }

    @Override
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
        return false;
    }
}

class ipAdapter extends RecyclerView.Adapter<ipAdapter.ViewHolder> {
    private List<PointOfInterest> ipDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public final View mView;
        public final TextView mContentView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = (TextView) view.findViewById(R.id.title);

        }
    }

    //empty constructor
    public ipAdapter() {

    }

    public void setIpDataset(List<PointOfInterest> ipDataset) {
        this.ipDataset = ipDataset;
    }
    // Provide a suitable constructor (depends on the kind of dataset)
    public ipAdapter(List<PointOfInterest> myDataset) {
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

        holder.mContentView.setText(ipDataset.get(position).title);
        Log.d("index", ipDataset.get(position).title + " size: " + ipDataset.size());

        //To expand an "item" in the recyclerview
        holder.mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                v.measure(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
                final int targetHeight = v.getMeasuredHeight();

                Log.d("TAG", Integer.toString(v.getMeasuredHeight()));

                // Older versions of android (pre API 21) cancel animations for views with a height of 0.
                v.getLayoutParams().height = 1;
                v.setVisibility(View.VISIBLE);

                Animation a = new Animation() {
                    @Override
                    protected void applyTransformation(float interpolatedTime, Transformation t) {
                        //Check to make it expanding more "adaptive way".
                        /*v.getLayoutParams().height = interpolatedTime == 1
                                ? RecyclerView.LayoutParams.WRAP_CONTENT
                                : (int) (targetHeight * interpolatedTime);*/
                        v.getLayoutParams().height = (int) (targetHeight * interpolatedTime * 10);

                        Log.d("TAG", "2: " + Integer.toString(v.getLayoutParams().height));

                        v.requestLayout();
                    }

                    @Override
                    public boolean willChangeBounds() {
                        return false;
                    }
                };

                // 1dp/ms
                a.setDuration(((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density)) * 4);
                v.startAnimation(a);
            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return ipDataset.size();
    }
}