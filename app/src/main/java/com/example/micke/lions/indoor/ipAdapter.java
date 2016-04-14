package com.example.micke.lions.indoor;

import android.graphics.Rect;
import android.content.Context;
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

import java.util.List;

public class ipAdapter extends RecyclerView.Adapter<ipAdapter.ViewHolder> {
    private List<PointOfInterest> ipDataset;
    public boolean isExpanded = false;
    private int temphHeight;
    private View tempView;
    private String TAG = "ipAdapter";
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
    public ipAdapter(Context con, List<PointOfInterest> myDataset) {
        mContext = con;
        ipDataset = myDataset;
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

        holder.mTitleView.setText(ipDataset.get(position).title);
        holder.mContentView.setText(ipDataset.get(position).getDescription());
        holder.mIDView.setText(ipDataset.get(position).getId());
        Log.d("index", ipDataset.get(position).title + " size: " + ipDataset.size());

        holder.goToMapImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewPager mPager = (ViewPager) v.getRootView().findViewById(R.id.container);
                mPager.setCurrentItem(0, true);
                ((IndoorActivity)mContext).map.highlightIP(((TextView)((View)v.getParent()).findViewById(R.id.id)).getText().toString());
            }
        });


        //To expand an "item" in the recyclerview
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                if(isExpanded){

                    if (tempView != null && tempView != v) {
                        collapseView(tempView);
                        expandView(v);
                    }

                    else if(tempView == v){
                        collapseView(v);
                        isExpanded = false;
                    }
                }

                //if(isExpanded == false)
                else{
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
        return ipDataset.size();
    }

    public void removeItem(int position) {
        ipDataset.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, ipDataset.size());

    }

    public void addItem(int position, PointOfInterest ip) {
        ipDataset.add(position, ip);
        notifyItemInserted(position);
        notifyItemRangeChanged(position, ipDataset.size());
    }

    public void moveItem(int fromPosition, int toPosition) {
        final PointOfInterest ip = ipDataset.remove(fromPosition);
        ipDataset.add(toPosition, ip);
        notifyItemMoved(fromPosition, toPosition);
    }

    public void updateAdapter(List<PointOfInterest> ipSet) {
        applyAndAnimateRemovals(ipSet);
        applyAndAnimateAdditions(ipSet);
        Log.d("new", "---");
        for(PointOfInterest ip: ipDataset){
            Log.d("new filtered list", ip.title);
        }

        applyAndAnimateMovedItems(ipSet);
    }

    private void applyAndAnimateRemovals(List<PointOfInterest> newips) {
        for (int i = ipDataset.size() - 1; i >= 0; i--) {
            final PointOfInterest ip = ipDataset.get(i);
            if (!newips.contains(ip)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<PointOfInterest> newips) {
        for (int i = 0, count = newips.size(); i < count; i++) {
            final PointOfInterest ip = newips.get(i);
            if (!ipDataset.contains(ip)) {
                addItem(i, ip);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<PointOfInterest> newips) {
        for (int toPosition = newips.size() - 1; toPosition >= 0; toPosition--) {
            final PointOfInterest ip = newips.get(toPosition);
            final int fromPosition = ipDataset.indexOf(ip);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

}
