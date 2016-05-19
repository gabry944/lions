package com.example.micke.lions.indoor;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.micke.lions.Common;
import com.example.micke.lions.R;

import java.util.ArrayList;
import java.util.List;

public class FloorAdapter extends RecyclerView.Adapter<FloorAdapter.ViewHolder> {
    private List<String> ipDataset;
    public boolean isExpanded = false;
    private int temphHeight;
    private View tempView;
    private String TAG = "FloorAdapter";
    private IndoorMapFragment mIndoorMapFragment;

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
    public FloorAdapter(IndoorMapFragment indoorMapFragment, List<String> myDataset) {
        mIndoorMapFragment = indoorMapFragment;
        ipDataset = new ArrayList<>();
    }

    public void setData(List<String> dataset) {
        ipDataset = new ArrayList<>();
        for(String s : dataset) {
            ipDataset.add("Våning " + s);
        }
        if(Common.IsAdmin())
            ipDataset.add(mIndoorMapFragment.getResources().getString(R.string.addfloor));
    }

    // Create new views (invoked by the layout manager)
    @Override
    public FloorAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_show_floors_list, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        holder.mContentView.setText(ipDataset.get(position));

        //To expand an "item" in the recyclerview
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String floor = ipDataset.get(position);

                if(!floor.equals(mIndoorMapFragment.getResources().getString(R.string.addfloor)))
                    floor = Character.toString(floor.charAt(floor.length()-1));
                else floor = "";

                if(!floor.equals(""))
                    mIndoorMapFragment.changeFloor(floor);
                else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    mIndoorMapFragment.getActivity().startActivityForResult(Intent.createChooser(intent,
                            "Select Picture"), 1);
                    Log.d("hejgallery", "called");
                }
            }
        });
    }

    public void resetData() {
        ipDataset = new ArrayList<>();
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return ipDataset.size();
    }

    public void removeItem(int position) {
        ipDataset.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, ipDataset.size());

    }

    public void addItem(int position, String ip) {
        ipDataset.add(position, ip);
        notifyItemInserted(position);
        notifyItemRangeChanged(position, ipDataset.size());
    }

    public void moveItem(int fromPosition, int toPosition) {
        final String ip = ipDataset.remove(fromPosition);
        ipDataset.add(toPosition, ip);
        notifyItemMoved(fromPosition, toPosition);
    }

    public void updateAdapter(List<String> ipSet) {
        applyAndAnimateRemovals(ipSet);
        applyAndAnimateAdditions(ipSet);
        Log.d("new", "---");
        for(String ip: ipDataset){
            Log.d("new filtered list", ip);
        }

        applyAndAnimateMovedItems(ipSet);
    }

    private void applyAndAnimateRemovals(List<String> newips) {
        for (int i = ipDataset.size() - 1; i >= 0; i--) {
            final String ip = ipDataset.get(i);
            if (!newips.contains(ip)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<String> newips) {
        for (int i = 0, count = newips.size(); i < count; i++) {
            final String ip = newips.get(i);
            if (!ipDataset.contains(ip)) {
                addItem(i, ip);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<String> newips) {
        for (int toPosition = newips.size() - 1; toPosition >= 0; toPosition--) {
            final String ip = newips.get(toPosition);
            final int fromPosition = ipDataset.indexOf(ip);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

}
