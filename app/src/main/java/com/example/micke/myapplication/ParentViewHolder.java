package com.example.micke.myapplication;

import android.view.View;
import android.widget.TextView;

/**
 * Created by mikaela on 16-04-05.
 */
public class ParentViewHolder {
    public final TextView mContentView;
    public final View mView;

    public ParentViewHolder(View view) {
        super();
        mView = view;
        mContentView = (TextView) view.findViewById(R.id.title);
    }
}
