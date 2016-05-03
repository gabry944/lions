package com.example.micke.lions.indoor;

import java.util.List;

public interface IndoorMapMarkerChange {
    void getUpdatedDataSet(List<PointOfInterest> pointOfInterestList);
    void dataSetChanged();
}
