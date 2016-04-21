package com.example.micke.lions.outdoor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
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

import com.example.micke.lions.R;
import com.example.micke.lions.indoor.IndoorActivity;
import com.example.micke.lions.indoor.PointOfInterest;

import java.util.List;

public class BuildingAdapter extends RecyclerView.Adapter<BuildingAdapter.ViewHolder> {
    private List<Building> buildingDataset;
    public boolean isExpanded = false;
    private int temphHeight;
    private View tempView;
    private String TAG = "BuildingAdapter";
    private Context mContext;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public final View mView;
        public final TextView mContentView;
        public final TextView mTitleView;
        public final ImageButton goToMapImage;
        public final TextView mIDView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = (TextView) view.findViewById(R.id.title);
            mContentView = (TextView) view.findViewById(R.id.content);
            goToMapImage = (ImageButton) view.findViewById(R.id.goToMapImage);
            mIDView = (TextView) view.findViewById(R.id.id);
        }
    }

    //empty constructor
    public BuildingAdapter(Context con, List<Building> myDataset) {
        mContext = con;
        buildingDataset = myDataset;
    }

    public void setBuildingDataset(List<Building> buildingDataset) {
        this.buildingDataset = buildingDataset;
    }
    // Provide a suitable constructor (depends on the kind of dataset)
    public BuildingAdapter(List<Building> myDataset) {
        buildingDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public BuildingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.building_list_view, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        holder.mTitleView.setText(buildingDataset.get(position).getName());
        holder.mIDView.setText(buildingDataset.get(position).getId());
        holder.goToMapImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewPager mPager = (ViewPager) v.getRootView().findViewById(R.id.container);
                mPager.setCurrentItem(0, true);
                ((OutdoorActivity) mContext).getFireBaseHandler()
                        .goToBuilding(buildingDataset.get(position).getId(), ((OutdoorActivity) mContext).map);
            }
        });


        //To expand an "item" in the recyclerview
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Intent intent = new Intent(mContext, IndoorActivity.class);
                Bundle bundle = new Bundle();
                String buildingId = holder.mIDView.getText().toString();
                Log.d("outdoor", "buldingId: " + buildingId);
                bundle.putString("buildingId", buildingId);
                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
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

        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density) * 4);
        v.startAnimation(a);

        v.findViewById(R.id.content).setVisibility(View.GONE);
    }

    public void expandView(final View v) {
        tempView = v;

        v.measure(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
        final int initHeight = v.getMeasuredHeight();
        temphHeight = initHeight;


        //must set to visible AFTER the extraction of the height
        TextView contentView = (TextView)v.findViewById(R.id.content);
        contentView.setVisibility(View.VISIBLE);

        //get length of text by bounds.width after these two lines
        Rect bounds = new Rect();
        contentView.getPaint().getTextBounds((String) contentView.getText(), 0, contentView.getText().length(), bounds);

        int cardwidth = v.getWidth() - 12 *4;
        int textHeight = v.getHeight() - 12 *2;

        Log.d(TAG, "expandView: bounds.width()/cardwidth = " + bounds.width()/cardwidth);
        Log.d(TAG, "expandView: (bounds.width()/cardwidth +1)*textHeight = " + (bounds.width()/cardwidth +1)*textHeight);
        //get desired size of card by calculating number of rows
        final int targetHeight = initHeight + (bounds.width()/cardwidth +1)*textHeight;

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);



        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                //Make it expand in a more "adaptive way".
                if (targetHeight/initHeight <2)
                    v.getLayoutParams().height = (int) (initHeight * interpolatedTime * 2);
                else
                    v.getLayoutParams().height = (int) (initHeight * interpolatedTime * (targetHeight/initHeight));
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }

        };

        // 1dp/ms
        a.setDuration(((int) (initHeight / v.getContext().getResources().getDisplayMetrics().density)) * 10);
        v.startAnimation(a);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return buildingDataset.size();
    }

    public void removeItem(int position) {
        buildingDataset.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, buildingDataset.size());

    }

    public void addItem(int position, Building building) {
        buildingDataset.add(position, building);
        notifyItemInserted(position);
        notifyItemRangeChanged(position, buildingDataset.size());
    }

    public void moveItem(int fromPosition, int toPosition) {
        final Building building = buildingDataset.remove(fromPosition);
        buildingDataset.add(toPosition, building);
        notifyItemMoved(fromPosition, toPosition);
    }

    public void updateAdapter(List<Building> buildingList) {
        applyAndAnimateRemovals(buildingList);
        applyAndAnimateAdditions(buildingList);
        applyAndAnimateMovedItems(buildingList);
    }

    private void applyAndAnimateRemovals(List<Building> buildings) {
        for (int i = buildingDataset.size() - 1; i >= 0; i--) {
            final Building building = buildingDataset.get(i);
            if (!buildings.contains(building)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<Building> buildings) {
        for (int i = 0, count = buildings.size(); i < count; i++) {
            final Building building = buildings.get(i);
            if (!buildingDataset.contains(building)) {
                addItem(i, building);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<Building> buildings) {
        for (int toPosition = buildings.size() - 1; toPosition >= 0; toPosition--) {
            final Building building = buildings.get(toPosition);
            final int fromPosition = buildingDataset.indexOf(building);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

}
