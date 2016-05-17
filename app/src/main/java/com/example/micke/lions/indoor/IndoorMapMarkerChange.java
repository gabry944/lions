package com.example.micke.lions.indoor;

import android.graphics.Bitmap;

import java.util.List;

public interface IndoorMapMarkerChange {
    void getMapimagesDataSet(List<FloorMapimage> mapimageList);
    void getUpdatedDataSet(List<PointOfInterest> pointOfInterestList);
    void dataSetChanged();
}
