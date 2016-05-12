package com.example.micke.lions.indoor;

import android.animation.ValueAnimator;
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

import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import android.widget.ImageView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.example.micke.lions.R;
import com.example.micke.lions.Common;

import java.util.Vector;

public class ipAdapter extends RecyclerView.Adapter<ipAdapter.ViewHolder> {
    public static final int HEADER = 0;
    public static final int CHILD = 1;

    private int NR_OF_CATEGORIES = 6;
    private List<PointOfInterest> ipDataset;
    ArrayList<Vector<PointOfInterest>> sortedDataset = new ArrayList<Vector<PointOfInterest>>(5);
    private int temphHeight;
    private View tempView;
    private String TAG = "ipAdapter";
    private Context mContext;
    private int originalHeight = 0;
    private int posHeader = 0;
    private int posChild =-1;
    boolean[] isExpanded = new boolean[NR_OF_CATEGORIES];
    private ArrayList<Vector<PointOfInterest>> sortdedListofIP2D;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
   /* public static class ViewHolder extends RecyclerView.ViewHolder {
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
    }*/

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


    //empty constructor
    public ipAdapter(Context con, List<PointOfInterest> myDataset) {
        Log.d(TAG, "ipAdapter: 1");
        mContext = con;
        ipDataset = myDataset;
        sortdedListofIP2D = new ArrayList<Vector<PointOfInterest>>(NR_OF_CATEGORIES);
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
        Log.d(TAG, "ipAdapter: 2");
        ipDataset = myDataset;
        sortdedListofIP2D = new ArrayList<Vector<PointOfInterest>>(NR_OF_CATEGORIES);

        updateSortedList();
    }

    // Create new views (invoked by the layout manager)
  /*  @Override
    public ipAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {

        switch(viewType){
            case HEADER:
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.fragment_indoor_list_header, parent, false);

            case CHILD:
        }
        // create a new view


        ViewHolder vh = new ViewHolder(v);
        return vh;
    }*/

    @Override
    public ipAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {

            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_indoor_list_header, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        // TODO: call function in another place. temporary solution
        if(position == 0){
            posHeader = 0;
        }

        Log.d(TAG, "onBindViewHolder: posChlide: " + posChild + ", posHeader: " + posHeader);
        if (posHeader < NR_OF_CATEGORIES && sortdedListofIP2D.get(posHeader) != null) {

            holder.header_title.setText(toName(posHeader));
            if (sortdedListofIP2D.get(posHeader).size() == 0) {
                holder.btn_expand_toggle.setImageResource(R.drawable.common_plus_signin_btn_text_light_disabled);
            } else {
                holder.btn_expand_toggle.setImageResource(R.drawable.add);
            }
            posHeader++;
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                LinearLayout linearLayout =  (LinearLayout) v.findViewById(R.id.layout);
                int index = ((ViewGroup) linearLayout.getParent()).indexOfChild(linearLayout);

                holder.btn_expand_toggle.setImageResource(R.drawable.navigation);
                if(!isExpanded[index]){
                    expandView(v, holder);
                    isExpanded[index] = true;
                }
                else{
                    holder.btn_expand_toggle.setImageResource(R.drawable.add);
                    collapseView(v);
                    isExpanded[index] = false;
                }

                int count = 0;
                /*if(list == null){
                    while (count < sortdedListofIP2D.get(posHeader).size()-1) {
                        list.add(sortdedListofIP2D.get(count).get(count));
                        count++;
                    }
                }*/


            }

        });



        //TODO temporär våningsvisare. Ful men praktisk. Fixa snyggare version
     /*   holder.mTitleView.setText(ipDataset.get(position).getTitle() + " (Våning " + ipDataset.get(position).getFloor() + ")");
        holder.mContentView.setText(ipDataset.get(position).getDescription());
        holder.mIDView.setText(ipDataset.get(position).getId());

        String category = ipDataset.get(position).getCategory();
        if(category.equals("Entré"))
            holder.goToMapImage.setImageResource( ipDataset.get(position).getOfficial() ? R.drawable.entrance_green : R.drawable.entrance_new );
        else if(category.equals("Hiss"))
            holder.goToMapImage.setImageResource( ipDataset.get(position).getOfficial() ? R.drawable.elevator_marker_green : R.drawable.elevator_new );
        else if (category.equals("Trappa"))
            holder.goToMapImage.setImageResource( ipDataset.get(position).getOfficial() ? R.drawable.stairs_green : R.drawable.stairs_menu );
        else if (category.equals("Toalett"))
            holder.goToMapImage.setImageResource( ipDataset.get(position).getOfficial() ? R.drawable.wc_green : R.drawable.wc);
        else
            holder.goToMapImage.setImageResource( ipDataset.get(position).getOfficial() ? R.drawable.map_marker_green : R.drawable.navigation);

        holder.goToMapImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewPager mPager = (ViewPager) v.getRootView().findViewById(R.id.container);
                mPager.setCurrentItem(0, true);
                ((IndoorActivity)mContext).map.highlightIP(ipDataset.get(position).getFloor(), ((TextView)((View)v.getParent()).findViewById(R.id.id)).getText().toString());
            }
        });


        //To expand an "item" in the recyclerview
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                ValueAnimator valueAnimator = ValueAnimator.ofInt(0);

                if(isExpanded){

                    if (tempView != null && tempView != v) {
                        collapseView(tempView, valueAnimator);
                        expandView(v, valueAnimator);
                    }

                    else if(tempView == v){
                        collapseView(v, valueAnimator);
                        isExpanded = false;
                    }
                }

                //if(isExpanded == false)
                else{
                    expandView(v, valueAnimator);
                    isExpanded = true;
                }
            }
        });*/
    }

    public void collapseView(final View v){
        LinearLayout linearLayout =  (LinearLayout) v.findViewById(R.id.layout);
        int index = ((ViewGroup) linearLayout.getParent()).indexOfChild(linearLayout);
        Log.d(TAG, "index collapse= " + index);
        linearLayout.removeViews(1,sortdedListofIP2D.get(index).size());
    }

    public void expandView(final View v, final ViewHolder holder){

        LinearLayout linearLayout =  (LinearLayout) v.findViewById(R.id.layout);
        final int index = ((ViewGroup) linearLayout.getParent()).indexOfChild(linearLayout);
        Log.d(TAG, "index expand= " + index);

        final LayoutInflater lyInflaterForPanel = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        for(int i = 0; i < sortdedListofIP2D.get(index).size(); i++){

            LinearLayout childLayout = (LinearLayout) lyInflaterForPanel.inflate(
                    R.layout.fragment_indoor_list_child, null);

            TextView item = (TextView) childLayout.findViewById(R.id.child_content);
            item.setText(sortdedListofIP2D.get(index).get(i).getTitle());

            ImageButton goToMapImage = (ImageButton) childLayout.findViewById(R.id.goToMapImage);
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
            /*goToMapImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewPager mPager = (ViewPager) v.getRootView().findViewById(R.id.container);
                    mPager.setCurrentItem(0, true);
                    ((IndoorActivity)mContext).map.highlightIP(sortdedListofIP2D.get(index).get(index2).getFloor(), ((TextView)((View)v.getParent()).findViewById(R.id.id)).getText().toString());
                }
            });*/

            linearLayout.addView(childLayout);
        }
    }

   /* public void collapseView(final View v) {
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
        v.findViewById(R.id.qr_code).setVisibility(View.GONE);
        v.findViewById(R.id.createQR).setVisibility(View.GONE);
    }*/



   /* public void expandView(final View v) {

        if (originalHeight == 0) {
            originalHeight = v.getHeight();
        }
        tempView = v;


        v.measure(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
        final int initHeight = v.getMeasuredHeight();
        temphHeight = initHeight;


        //must set to visible AFTER the extraction of the height
        TextView contentView = (TextView)v.findViewById(R.id.content);
        TextView qrContentView = (TextView) v.findViewById(R.id.qr_code);
        ImageButton qrButton = (ImageButton) v.findViewById(R.id.createQR);
        contentView.setVisibility(View.VISIBLE);



        //get length of text by bounds.width after these two lines
        Rect bounds_description = new Rect();
        contentView.getPaint().getTextBounds((String) contentView.getText(), 0, contentView.getText().length(), bounds_description);

        Rect bounds_qr = new Rect();

        if(Common.IsAdmin()) {
            qrContentView.getPaint().getTextBounds((String) qrContentView.getText(), 0, qrContentView.getText().length(), bounds_qr);
        }


        int cardwidth = v.getWidth() - 12 *4;
        int textHeight = v.getHeight() - 12 *2;

        Log.d(TAG, "expandView: bounds.width()/cardwidth = " + bounds_description.width()/cardwidth);
        Log.d(TAG, "expandView: (bounds.width()/cardwidth +1)*textHeight = " + (bounds_description.width()/cardwidth +1)*textHeight);
        //get desired size of card by calculating number of rows
        final int targetHeight;
        if(Common.IsAdmin()){
            qrContentView.setVisibility(View.VISIBLE);
            qrButton.setVisibility(View.VISIBLE);
            targetHeight = initHeight + (bounds_description.width()/cardwidth +1)*textHeight + (bounds_qr.width()/cardwidth + 1)*textHeight + 100;
            Log.d(TAG, "expandView: bounds.width()/cardwidth = " + targetHeight);
        }
        else
            targetHeight = initHeight + (bounds_description.width()/cardwidth +1)*textHeight;

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
    }*/

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        updateSortedList();
        return  NR_OF_CATEGORIES;
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

    private void clearSortedList()
    {
        Log.d(TAG, "clearSortedList: ");
        sortdedListofIP2D.clear();

        //fill list
        for (int i=0;i<NR_OF_CATEGORIES;i++)
            sortdedListofIP2D.add(new Vector<PointOfInterest>());
    }
    private void updateSortedList()
    {
        Log.d(TAG, "updateSortedList: ipDataset size = " + ipDataset.size() );
        clearSortedList();
        for (PointOfInterest p: ipDataset) {
           // Log.d(TAG, "updateSortedList: fetch from ipDataset");
            String cat = p.getCategory();
            //turn category name to numer like 0, 1, 2, ...
            int place1 = toPlace(cat);
            if (place1 == -1)
                Log.d(TAG, "ipAdapter: något har gått fel nät vi konverterade categorier till int. cat = " + cat);
            if (sortdedListofIP2D.get(place1) == null){
                sortdedListofIP2D.set(place1, new Vector<PointOfInterest>());
                sortdedListofIP2D.get(place1).add(p);
            }
            else
                sortdedListofIP2D.get(place1).add(p);
        }
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
    private int toPlace(String s)
    {
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
        else
            return -1;
    }

    private String toName(int s)
    {
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
        else
            return null;
    }



    public static class Item {
        public int type;
        public String title;
        public String description;
        public List<Item> invisibleChildren;

        public Item() {
        }
        public Item(int type, String title) {
            this.type = type;
            this.title = title;
        }
        public Item(int type, String title, String description) {
            this.type = type;
            this.title = title;
            this.description = description;
        }
    }
}

