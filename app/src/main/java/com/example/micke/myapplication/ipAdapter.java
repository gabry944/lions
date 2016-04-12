package com.example.micke.myapplication;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.TextView;

import java.util.List;

/**
 * Created by micke on 2016-04-12.
 */
public class ipAdapter extends RecyclerView.Adapter<ipAdapter.ViewHolder> {
    private List<PointOfInterest> ipDataset;
    public boolean isExpanded = false;
    private int temphHeight;
    private View tempView;

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

                if (isExpanded) {
                    /*if(tempView != null)
                        collapseView(tempView);*/

                    collapseView(v);
                    isExpanded = false;
                } else {
                    expandView(v);
                    isExpanded = true;
                }

            }
        });

    }

    public void collapseView(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {

                v.getLayoutParams().height = (initialHeight + temphHeight) - (int) (initialHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }

        };

        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);

    }

    public void expandView(final View v) {
        tempView = v;

        v.measure(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();
        temphHeight = targetHeight;

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                //Make it expand in a more "adaptive way".
                v.getLayoutParams().height = (int) (targetHeight * interpolatedTime * 10);

                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }

        };

        // 1dp/ms
        a.setDuration(((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density)) * 10);
        v.startAnimation(a);

    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return ipDataset.size();
    }
}
