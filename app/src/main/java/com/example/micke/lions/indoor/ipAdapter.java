package com.example.micke.lions.indoor;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;
import java.util.ArrayList;
import java.util.List;

import com.example.micke.lions.R;

import java.util.Vector;

public class ipAdapter extends RecyclerView.Adapter<ipAdapter.ViewHolder> {

    private int NR_OF_CATEGORIES = 7;
    private List<PointOfInterest> ipDataset;
    private String TAG = "ipAdapter";
    private Context mContext;
    boolean[] isExpanded = new boolean[NR_OF_CATEGORIES];
    private ArrayList<Vector<PointOfInterest>> sortdedListofIP2D;
    private ArrayList<View> categoriesViews;
    

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView header_title;
        public ImageView btn_expand_toggle;
        public PointOfInterest pointOfInterest;
        public final View mView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            header_title = (TextView) itemView.findViewById(R.id.header_title);
            btn_expand_toggle = (ImageView) itemView.findViewById(R.id.expand_button);
        }
    }

    public ipAdapter(Context con, List<PointOfInterest> myDataset) {
        Log.d(TAG, "constructor 1");
        mContext = con;
        ipDataset = myDataset;
        sortdedListofIP2D = new ArrayList<Vector<PointOfInterest>>(NR_OF_CATEGORIES);
        categoriesViews = new ArrayList<View>(NR_OF_CATEGORIES);
        //fill list
        for (int i=0;i<NR_OF_CATEGORIES;i++)
            categoriesViews.add(null);
        Log.d(TAG, "constructor 1: cat size = " + categoriesViews.size());
        Log.d(TAG, "constructor 1: sort size = " + sortdedListofIP2D.size());
        for(int i = 0; i < isExpanded.length; i++){
            isExpanded[i] = false;
        }

        updateSortedList();
    }

    public void setIpDataset(List<PointOfInterest> ipDataset) {
        this.ipDataset = ipDataset;
        updateSortedList();
    }
    // Provide a suitable constructor (depends on the kind of dataset)
    public ipAdapter(List<PointOfInterest> myDataset) {
        Log.d(TAG, "constructor 2");
        ipDataset = myDataset;
        sortdedListofIP2D = new ArrayList<Vector<PointOfInterest>>(NR_OF_CATEGORIES);
        categoriesViews = new ArrayList<View>(NR_OF_CATEGORIES);

        updateSortedList();
    }

    @Override
    public ipAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        Log.d(TAG, "onCreateViewHolder: viewType = " + viewType);
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_indoor_list_header, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        Log.d(TAG, "onBindViewHolder: pos = " + position);
        //Log.d(TAG, "onCreateViewHolder: cat size = " + categoriesViews.size());
        //Log.d(TAG, "onCreateViewHolder: sort size = " + sortdedListofIP2D.size());

        if (position < NR_OF_CATEGORIES && sortdedListofIP2D.get(position) != null) {

            categoriesViews.set(position, holder.mView);

            holder.header_title.setText(toName(position));
            //if (sortdedListofIP2D.get(position).size() == 0) {
            //    holder.btn_expand_toggle.setVisibility(View.INVISIBLE);
            //} else {
                holder.btn_expand_toggle.setImageResource(R.drawable.arrow_down);
            //}
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //LinearLayout linearLayout =  (LinearLayout) v.findViewById(R.id.layout);
                //int index = ((ViewGroup) linearLayout.getParent()).indexOfChild(linearLayout);
                int index = categoriesViews.indexOf(v);

                holder.btn_expand_toggle.setImageResource(R.drawable.arrow_up);
                if(!isExpanded[index]){
                    expandView(v);
                    isExpanded[index] = true;
                }
                else{
                    holder.btn_expand_toggle.setImageResource(R.drawable.arrow_down);
                    collapseView(v);
                    isExpanded[index] = false;
                }
            }

        });
    }

    public void collapseView(final View v){
        LinearLayout linearLayout =  (LinearLayout) v.findViewById(R.id.layout);
        /*int index = ((ViewGroup) linearLayout.getParent()).indexOfChild(linearLayout);
        Log.d(TAG, "collapseView: index förut = " + index);*/
        int index = categoriesViews.indexOf(v);
        Log.d(TAG, "collapseView: index = " + index);
        linearLayout.removeViews(1,sortdedListofIP2D.get(index).size());
    }

    public void expandView(final View v){

        LinearLayout linearLayout =  (LinearLayout) v.findViewById(R.id.layout);
        /*final int index33 = ((ViewGroup) linearLayout.getParent()).indexOfChild(linearLayout);
        Log.d(TAG, "collapseView: index förut = " + index33);*/
        final int index = categoriesViews.indexOf(v);
        Log.d(TAG, "expandView: index = " + index);
        TextView header_title = (TextView) v.findViewById(R.id.header_title);
        Log.d(TAG, "expandView: categorie: " + header_title.getText());

        final LayoutInflater lyInflaterForPanel = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        Log.d(TAG, "expandView: sortdedListofIP2D.get(index).size() = " + sortdedListofIP2D.get(index).size());
        for(int i = 0; i < sortdedListofIP2D.get(index).size(); i++){

            LinearLayout childLayout = (LinearLayout) lyInflaterForPanel.inflate(
                    R.layout.fragment_indoor_list_child, null);

            TextView item = (TextView) childLayout.findViewById(R.id.child_content);
            ImageButton goToMapImage = (ImageButton) childLayout.findViewById(R.id.goToMapImage);
            TextView idText = (TextView) childLayout.findViewById(R.id.id);

            idText.setText(sortdedListofIP2D.get(index).get(i).getId());
            item.setText(sortdedListofIP2D.get(index).get(i).getTitle());
            Log.d(TAG, "expandView: Title" + sortdedListofIP2D.get(index).get(i).getTitle());
            if(toName(index).equals(mContext.getResources().getString(R.string.ConferenceRoom)))
                goToMapImage.setImageResource(sortdedListofIP2D.get(index).get(i).getOfficial() ? R.drawable.map_marker_green : R.drawable.navigation);
            else if(toName(index).equals(mContext.getResources().getString(R.string.Entrance)))
                goToMapImage.setImageResource(sortdedListofIP2D.get(index).get(i).getOfficial() ? R.drawable.entrance_green : R.drawable.entrance_new );
            else if(toName(index).equals(mContext.getResources().getString(R.string.Toilet)))
                goToMapImage.setImageResource(sortdedListofIP2D.get(index).get(i).getOfficial() ? R.drawable.wc_green : R.drawable.wc );
            else if(toName(index).equals(mContext.getResources().getString(R.string.Elevator)))
                goToMapImage.setImageResource(sortdedListofIP2D.get(index).get(i).getOfficial() ? R.drawable.elevator_marker_green : R.drawable.elevator_new );
            else if(toName(index).equals(mContext.getResources().getString(R.string.Stairs)))
                goToMapImage.setImageResource(sortdedListofIP2D.get(index).get(i).getOfficial() ? R.drawable.stairs_green : R.drawable.stairs_menu );
            else
                goToMapImage.setImageResource( sortdedListofIP2D.get(index).get(i).getOfficial() ? R.drawable.map_marker_green : R.drawable.navigation);


            final int index2 = i;
            goToMapImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewPager mPager = (ViewPager) v.getRootView().findViewById(R.id.container);
                    mPager.setCurrentItem(0, true);
                    ((IndoorActivity)mContext).map.startWayFinding(sortdedListofIP2D.get(index).get(index2).getFloor(), ((TextView)((View)v.getParent()).findViewById(R.id.id)).getText().toString());
                }
            });
            childLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView detailInfo = (TextView) v.findViewById(R.id.child_detail_content);
                    if(detailInfo.getVisibility() == View.GONE){
                        detailInfo.setVisibility(View.VISIBLE);
                        detailInfo.setText(sortdedListofIP2D.get(index).get(index2).getDescription());
                    }
                    else
                    {
                        detailInfo.setVisibility(View.GONE);
                    }
                }
            });

            linearLayout.addView(childLayout);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        //Log.d(TAG, "getItemCount called");
        updateSortedList();
        return  NR_OF_CATEGORIES;
    }

    public void removeItem(int position) {
        ipDataset.remove(position);
        //notifyItemRemoved(position);
        //notifyItemRangeChanged(position, ipDataset.size());
        updateSortedList();
        //notifyDataSetChanged();

    }

    public void addItem(int position, PointOfInterest ip) {
        ipDataset.add(position, ip);
        //notifyItemInserted(position);
        //notifyItemRangeChanged(position, ipDataset.size());
        updateSortedList();
        //notifyDataSetChanged();
    }

    public void moveItem(int fromPosition, int toPosition) {
        final PointOfInterest ip = ipDataset.remove(fromPosition);
        ipDataset.add(toPosition, ip);
        //notifyItemMoved(fromPosition, toPosition);
        updateSortedList();
        //notifyDataSetChanged();
    }

    public void updateAdapter(List<PointOfInterest> ipSet) {
        Log.d(TAG, "updateAdapter: innan update");

        //update list
        applyAndAnimateRemovals(ipSet);
        applyAndAnimateAdditions(ipSet);
        applyAndAnimateMovedItems(ipSet);
        //notifyDataSetChanged();

        //updateSortedList();
        printSortedList();
        for (View v: categoriesViews)
        {
            TextView header_title = (TextView) v.findViewById(R.id.header_title);
            Log.d(TAG, "updateAdapter: categoriesViews: " + header_title.getText());
        }

        //expand categories
        //collapseAllCategories(); // collapse all first sinse need to reload the point under the categories
        expandAllCategories();

        Log.d(TAG, "updateAdapter: efter update");
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

    private void expandAllCategories(){
        View v;
        for(int i = 0; i<NR_OF_CATEGORIES; i++)
        {
            if(!isExpanded[i])
            {
                v = categoriesViews.get(i);
                ImageView mImageButton = (ImageView) v.findViewById(R.id.expand_button);
                mImageButton.setImageResource(R.drawable.arrow_up);
                //Log.d(TAG, "expandAllCategories: v = " + v);
                expandView(v);
                isExpanded[i] = true;
            }
        }
    }

    private void collapseAllCategories(){
        View v;
        for(int i = 0; i<NR_OF_CATEGORIES; i++)
        {
            if(isExpanded[i])
            {
                v = categoriesViews.get(i);
                ImageView mImageButton = (ImageView) v.findViewById(R.id.expand_button);
                mImageButton.setImageResource(R.drawable.arrow_up);
                //Log.d(TAG, "expandAllCategories: v = " + v);
                collapseView(v);
                isExpanded[i] = false;
            }
        }
    }

    private void clearSortedList() {
        sortdedListofIP2D.clear();

        //fill list
        for (int i=0;i<NR_OF_CATEGORIES;i++)
            sortdedListofIP2D.add(new Vector<PointOfInterest>());
    }
    private void updateSortedList() {
        //Log.d(TAG, "updateSortedList: ipDataset size = " + ipDataset.size() );
        clearSortedList();
        for (PointOfInterest p: ipDataset) {
           // Log.d(TAG, "updateSortedList: fetch from ipDataset");
            String cat = p.getCategory();
            //turn category name to number like 0, 1, 2, ...
            int place1 = toPlace(cat);
            if (place1 == -1)
                Log.d(TAG, "ipAdapter: något har gått fel nät vi konverterade categorier till int. Ttitle: " + p.getTitle() + " cat = " + cat);
            if (sortdedListofIP2D.get(place1) == null){
                sortdedListofIP2D.set(place1, new Vector<PointOfInterest>());
                sortdedListofIP2D.get(place1).add(p);
            }
            else
                sortdedListofIP2D.get(place1).add(p);
        }
        //Log.d(TAG, "updatesortedlist succeeded with " + sortdedListofIP2D.size());
       // printSortedList();
    }
    private void printSortedList(){
        Log.d(TAG, "printSortedList: size = " + sortdedListofIP2D.size());
        for(int i=0; i < NR_OF_CATEGORIES; i++){
            Log.d(TAG, "printSortedList: i = " + i);
            if(sortdedListofIP2D.get(i)!=null){
                for (PointOfInterest p: sortdedListofIP2D.get(i)){
                    Log.d(TAG, "printSortedList: " + p.getTitle() + "   cat: " + p.getCategory());
                }
            }
        }
    }
    private int toPlace(String s) {
        if(s.equals(mContext.getResources().getString(R.string.ConferenceRoom)))
            return 0;
        else if(s.equals(mContext.getResources().getString(R.string.Entrance)))
            return 1;
        else if(s.equals(mContext.getResources().getString(R.string.Toilet)))
            return 2;
        else if(s.equals(mContext.getResources().getString(R.string.Printer)))
            return 3;
        else if(s.equals(mContext.getResources().getString(R.string.Elevator)))
            return 4;
        else if(s.equals(mContext.getResources().getString(R.string.Stairs)))
            return 5;
        else if(s.equals(mContext.getResources().getString(R.string.Other)))
            return 6;
        else
            return -1;
    }

    private String toName(int s) {
        if(s == 0 )
            return mContext.getResources().getString(R.string.ConferenceRoom);
        else if(s == 1 )
            return mContext.getResources().getString(R.string.Entrance);
        else if(s == 2 )
            return mContext.getResources().getString(R.string.Toilet);
        else if(s == 3 )
            return mContext.getResources().getString(R.string.Printer);
        else if(s == 4 )
            return mContext.getResources().getString(R.string.Elevator);
        else if(s == 5 )
            return mContext.getResources().getString(R.string.Stairs);
        else if(s == 6 )
            return mContext.getResources().getString(R.string.Other);
        else
            return null;
    }

}

