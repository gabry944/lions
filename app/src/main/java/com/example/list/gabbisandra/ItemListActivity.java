package com.example.list.gabbisandra;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.example.list.gabbisandra.dummy.DummyContent;
import com.example.list.gabbisandra.Datasource;
import com.example.list.gabbisandra.Item;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.List;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    static final int CONTACT_TO_DETAIL_VIEW = 1;
    public static final String PREFS_ORDER = "MyPrefsOrder";
    private boolean mTwoPane;
    private ActionMode mActionMode = null;
    private Datasource mDatasource;
    private SimpleItemRecyclerViewAdapter adapter;
    private View recyclerView;
    private int mSort = 0;
    private boolean mAssend = true;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.add:
                mDatasource.insertItem("fisk", 3, "blubb");
                changeRecyclerView((RecyclerView) recyclerView);

                return true;
            case R.id.ascending:

                if(item.isChecked())
                    mAssend = false;
                else
                    mAssend = true;

                item.setChecked(mAssend);
                changeRecyclerView((RecyclerView)recyclerView);

                return true;
            case R.id.sorting:
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                FireMissilesDialogFragment dialog = new FireMissilesDialogFragment();
                dialog.show(ft, "test");

            return true;
            case R.id.help:
                Log.i("HELP: ","Help tryckt LIST!!");
                return true;
            default:
                Log.i("delete", "något okänt tryckt LIST!!");

                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences settings = getSharedPreferences(PREFS_ORDER, 0);
        mAssend = settings.getBoolean("assend", true);
        mSort = settings.getInt("sort", 0);

        setContentView(R.layout.activity_item_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.alt1, menu);

        MenuItem assendingItem = menu.findItem(R.id.ascending);
        assendingItem.setChecked(mAssend);
        return true;
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        mDatasource = new Datasource(this);
        mDatasource.open();
        adapter = new SimpleItemRecyclerViewAdapter(mDatasource.fetchAll(mSort, mAssend));
        recyclerView.setAdapter(adapter);
    }

    private void changeRecyclerView(@NonNull RecyclerView recyclerView) {
        adapter = new SimpleItemRecyclerViewAdapter(mDatasource.fetchAll(mSort, mAssend));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "ItemList Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.list.gabbisandra/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        SharedPreferences settings = getSharedPreferences(PREFS_ORDER, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("assend", mAssend);
        editor.putInt("sort", mSort);

        editor.commit();


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "ItemList Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.list.gabbisandra/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<Item> mValues;

        public SimpleItemRecyclerViewAdapter(List<Item> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mIdView.setText(mValues.get(position).id + "");
            holder.mContentView.setText(mValues.get(position).getDescription());
            holder.

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(ItemDetailFragment.ARG_ITEM_ID, holder.mItem.id + "");
                        arguments.putString(ItemDetailFragment.ARG_ITEM_TITLE, holder.mItem.getTitle());
                        arguments.putString(ItemDetailFragment.ARG_ITEM_RAITING, holder.mItem.getRating() + "");
                        arguments.putString(ItemDetailFragment.ARG_ITEM_DESCRIPTION, holder.mItem.getDescription());

                        ItemDetailFragment fragment = new ItemDetailFragment();
                        fragment.setArguments(arguments);

                        if(mActionMode != null)
                            mActionMode.finish();
                        //getSupportFragmentManager().popBackStack();


                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.item_detail_container, fragment)
                                .addToBackStack(null)
                                .commit();

                        mActionMode = startSupportActionMode(mActionModeCallback);

                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, ItemDetailActivity.class);
                        intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, holder.mItem.id + "");
                        intent.putExtra(ItemDetailFragment.ARG_ITEM_TITLE, holder.mItem.getTitle());
                        intent.putExtra(ItemDetailFragment.ARG_ITEM_RAITING, holder.mItem.getRating() + "");
                        intent.putExtra(ItemDetailFragment.ARG_ITEM_DESCRIPTION, holder.mItem.getDescription());

                        //context.startActivity(intent);
                        startActivityForResult(intent,CONTACT_TO_DETAIL_VIEW);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public Item mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == CONTACT_TO_DETAIL_VIEW) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Log.i("id list", data.getIntExtra("id", -1) + "");
                deletePost(data.getIntExtra("id", -1));

                changeRecyclerView((RecyclerView) recyclerView);
            }
        }
    }

    public void deletePost(int id)
    {
        mDatasource.deleteItem(id);
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.alt2, menu);
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            int id = item.getItemId();

            if (id == R.id.delete) {
                Log.i("Delete: ", "Delete tryckt LIST!!");

                //hämta vilken post som skall bort
                ItemDetailFragment fragment = (ItemDetailFragment) getSupportFragmentManager().getFragments().get(getSupportFragmentManager().getFragments().size()-1);
                Bundle b = fragment.getArguments();
                String idnn = b.getString(ItemDetailFragment.ARG_ITEM_ID);
                Log.i("test2", "id: " + idnn);

                //ta bort post och fragment
                deletePost(Integer.parseInt(idnn));
                changeRecyclerView((RecyclerView)recyclerView);

                mActionMode.finish();

                return true;
            }

            return false;
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            getSupportFragmentManager().popBackStack();
            mActionMode = null;
        }
    };

    public class FireMissilesDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setTitle("Sorting order")
                   .setItems(R.array.sorting_options, new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int which) {
                           // The 'which' argument contains the index position
                           // of the selected item
                           mSort = which;
                           changeRecyclerView((RecyclerView) recyclerView);
                       }
                   });

            // Create the AlertDialog object and return it
            return builder.create();
        }
    }
}
