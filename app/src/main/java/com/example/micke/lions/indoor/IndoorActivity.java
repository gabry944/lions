package com.example.micke.lions.indoor;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.micke.lions.Common;
import com.example.micke.lions.LoginDialogFragment;
import com.example.micke.lions.outdoor.BuildingAdapter;
import com.example.micke.lions.R;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.List;


public class IndoorActivity extends AppCompatActivity {

    private String TAG = "IndoorActivity";

    private FireBaseIndoor fireBaseHandler;
    private IndoorPageSliderAdapter mSectionsPagerAdapter;
    public static ViewPager mViewPager;

    private String ipId;
    private String currentBuilding;
    private String buildingId;
    public IndoorMapFragment map;
    public IndoorListFragment list;
    public IndoorQRFragment qr;
    public List<PointOfInterest> myDataset;
    public BuildingAdapter buildingAdapter;
    public String youAreHereID = "";
    public String startGoalID = "";
    public String startGoalFloor = "";
    public String startFloor = "";
    private android.support.v7.app.ActionBar actionBar;
    public MenuItem adminButton;
    private String selectedImagePath = ""; //Used by admin when adding a map from gallery

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_indoor);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //get the building and set the connection to firebase
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        buildingId = bundle.getString("buildingId", "1");
        currentBuilding = bundle.getString("buildingTitle");
        Log.d("indoor", "buldingId: " + buildingId);
        fireBaseHandler = new FireBaseIndoor(getApplicationContext(), buildingId);

        actionBar = getSupportActionBar();

        if(actionBar != null){
            actionBar.setTitle(currentBuilding);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new IndoorPageSliderAdapter(getSupportFragmentManager(), this);



        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(1);
        //Listener that keeps track on which page(fragment) is showing etc.
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                actionBar.setDisplayHomeAsUpEnabled(position == 1);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //see if an IP was sent to the activity
        ipId = bundle.getString("ipId", "-1");

        if(!ipId.equals("-1")) {
            //An IP is found, the user is here, go to indoor map
            youAreHereID = ipId;
            startFloor = bundle.getString("floor");
            mViewPager.setCurrentItem(0);
        }

        //see if the user already set a goal
        startGoalID = bundle.getString("goalID", "");
        startGoalFloor = bundle.getString("goalFloor", "");
    }

    public FireBaseIndoor getFireBaseHandler() { return fireBaseHandler; }

    public String getBuildingId() {
        return buildingId;
    }

    @Override
    public void onBackPressed()
    {
        if(mViewPager.getCurrentItem() != 1)
            mViewPager.setCurrentItem(1);
        else
            super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_indoor_activity, menu);
        adminButton = menu.findItem(R.id.admin);
        Common.setAdminButton(adminButton, this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id ==  R.id.admin){
            if (Common.IsAdmin()) {
                Common.LogOut(map, list, qr);
                Common.setAdminButton(adminButton, this);
            }
            else {
                LoginDialogFragment login = new LoginDialogFragment();
                login.show(getFragmentManager(), "login_fragment");
            }
        }

        //Finishes activity and starting outdoorActivity.
        if(id == android.R.id.home) {
            onBackPressed();
            this.finish();
            return true;
        }

        return false;
    }

    //Called when image has been loaded from gallery by admin
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Uri selectedImageUri = data.getData();
                selectedImagePath = getPath(selectedImageUri);
                BitmapDrawable b = new BitmapDrawable(getResources(), selectedImagePath);
                if(b.getIntrinsicWidth() * b.getIntrinsicHeight() < 1000000) {
                    try {
                        map.fireBaseIndoor.addMap(getBitmapFromUri(selectedImageUri), map.nextFloorToAdd());
                        Log.d("hejgal", "uploaded image");
                    } catch (IOException e) {
                        Log.d("hejgal", "error reading image");
                    }
                }
                else {
                    Toast toast = Toast.makeText(this, "Image too large! Compressing...", Toast.LENGTH_LONG);
                    toast.show();
                    try {
                        map.fireBaseIndoor.addMap(getResizedBitmap(getBitmapFromUri(selectedImageUri), 1000), map.nextFloorToAdd());
                        Log.d("hejgal", "uploaded image");
                    } catch (IOException e) {
                        Log.d("hejgal", "error reading image");
                    }
                }
            }
        }
    }


    //helper to retrieve the path of an image URI
    public String getPath(Uri uri) {
        // just some safety built in
        if( uri == null ) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        // this is our fallback here
        return uri.getPath();
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
            ParcelFileDescriptor parcelFileDescriptor =
                    getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            return image;
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
}
