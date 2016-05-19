package com.example.micke.lions.indoor;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.micke.lions.Common;
import com.example.micke.lions.R;

import java.util.List;

/**
 * Created by hp1 on 28-12-2014.
 */
public class FloorDrawerAdapter extends RecyclerView.Adapter<FloorDrawerAdapter.ViewHolder> {

    private static final int TYPE_HEADER = 0;  // Declaring Variable to Understand which View is being worked on
    // IF the view under inflation and population is header or Item
    private static final int TYPE_ITEM = 1;

    private List<String> myDataSet;
    private Context mContext;
    private IndoorMapFragment mIndoorMapFragment;

    // Creating a ViewHolder which extends the RecyclerView View Holder
    // ViewHolder are used to to store the inflated views in order to recycle them

    public static class ViewHolder extends RecyclerView.ViewHolder {
        int Holderid;

        View mView;
        TextView textView;
        TextView id;

        public ViewHolder(View itemView, int ViewType) {                 // Creating ViewHolder Constructor with View and viewType As a parameter
            super(itemView);

            mView = itemView;
            // Here we set the appropriate view in accordance with the the view type as passed when the holder object is created
            if (ViewType == TYPE_ITEM) {
                textView = (TextView) itemView.findViewById(R.id.title);
                id = (TextView) itemView.findViewById(R.id.id);
                Holderid = 1;
            } else {
                Holderid = 0;
            }
        }
    }


    public FloorDrawerAdapter(IndoorMapFragment indoorMapFragment, List<String> dataset, Context context) {
        mIndoorMapFragment = indoorMapFragment;
        myDataSet = dataset;
        mContext = context;
    }

    //Below first we ovverride the method onCreateViewHolder which is called when the ViewHolder is
    //Created, In this method we inflate the item_row.xml layout if the viewType is Type_ITEM or else we inflate header.xml
    // if the viewType is TYPE_HEADER
    // and pass it to the view holder

    @Override
    public FloorDrawerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_show_floors_list, parent, false); //Inflating the layout
            ViewHolder vhItem = new ViewHolder(v, viewType); //Creating ViewHolder and passing the object of type view

            return vhItem; // Returning the created object
        } else if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.nav_header_navigationdrawertest, parent, false); //Inflating the layout
            ViewHolder vhHeader = new ViewHolder(v, viewType); //Creating ViewHolder and passing the object of type view

            return vhHeader; //returning the object created
        }
        return null;
    }

    //Next we override a method which is called when the item in a row is needed to be displayed, here the int position
    // Tells us item at which position is being constructed to be displayed and the holder id of the holder object tell us
    // which view type is being created 1 for item row
    @Override
    public void onBindViewHolder(FloorDrawerAdapter.ViewHolder holder, final int position) {
        if (holder.Holderid == 1) {                              // as the list view is going to be called after the header view so we decrement the
            // position by 1 and pass it to the holder while setting the text and image
            holder.textView.setText(myDataSet.get(position-1)); // Setting the Text with the array of our Titles
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    String floor = myDataSet.get(position-1);

                    if(!floor.equals(mIndoorMapFragment.getResources().getString(R.string.addfloor)))
                        floor = Character.toString(floor.charAt(floor.length()-1));
                    else
                        floor = "";

                    if(!floor.equals("")) {
                        mIndoorMapFragment.changeFloor(floor);
                    } else if(Common.IsReadMediaPermitted(mContext)) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        mIndoorMapFragment.getActivity().startActivityForResult(Intent.createChooser(intent,
                                "Select Picture"), 1);
                    }
                    else {
                        Toast toast = Toast.makeText(mContext, "Tillåt appen att läsa dina filer för att lägga upp en planlösning", Toast.LENGTH_LONG);
                        toast.show();
                    }
                }
            });
        } else {
//            holder.profile.setImageResource(profile);           // Similarly we set the resources for header view
//            holder.Name.setText(name);
//            holder.email.setText(email);
        }
    }

    // This method returns the number of items present in the list
    @Override
    public int getItemCount() {
        return myDataSet.size()+1; // the number of items in the list will be +1 the titles including the header view.
//        return myDataSet.size();
    }

    // Witht the following method we check what type of view is being passed
    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }
}
