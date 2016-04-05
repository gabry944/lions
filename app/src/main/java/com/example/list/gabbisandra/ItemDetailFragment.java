package com.example.list.gabbisandra;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.list.gabbisandra.dummy.DummyContent;

import java.util.zip.Inflater;
import com.example.list.gabbisandra.ItemListActivity;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class ItemDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    public static final String ARG_ITEM_TITLE = "item_title";
    public static final String ARG_ITEM_RAITING = "item_rating";
    public static final String ARG_ITEM_DESCRIPTION = "item_description";

    /**
     * The dummy content this fragment is presenting.
     */
    //private DummyContent.DummyItem mItem;
    private Item mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {
    }

    @Override
    // ta bort helt?
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.delete:
                Log.i("Delete: ","Delete tryckt FRAGMENT!!");
                return true;
            case R.id.edit:
                //showHelp();
                Log.i("edit", "something ");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        if (getArguments().containsKey(ARG_ITEM_ID)&& getArguments().containsKey(ARG_ITEM_TITLE) && getArguments().containsKey(ARG_ITEM_DESCRIPTION)&& getArguments().containsKey(ARG_ITEM_RAITING)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            //mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
            mItem = new Item();
            mItem.setId(Long.parseLong(getArguments().getString(ARG_ITEM_ID)));
            mItem.setTitle(getArguments().getString(ARG_ITEM_TITLE));
            mItem.setRating(Integer.parseInt(getArguments().getString(ARG_ITEM_RAITING)));
            mItem.setDescription(getArguments().getString(ARG_ITEM_DESCRIPTION));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.getTitle());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //View rootView = inflater.inflate(R.layout.item_detail, container, false);
        View rootView = inflater.inflate(R.layout.detailview, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            //((TextView) rootView.findViewById(R.id.item_detail)).setText(mItem.getTitle() + "\n \n" + mItem.getDescription() + "\n \n Rating: " + mItem.getRating());
            ((TextView) rootView.findViewById(R.id.titelTextView)).setText(mItem.getTitle());
            ((RatingBar) rootView.findViewById(R.id.ratingBar)).setRating(mItem.getRating());
            ((TextView) rootView.findViewById(R.id.descriptionTextView)).setText(mItem.getDescription());
        }

        return rootView;
    }



}
