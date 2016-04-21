package com.example.micke.lions.indoor;

import java.util.List;

/**
 * Created by Gabriella on 2016-04-21.
 */
public interface IndoorMapMarkerChange {
    void getUpdatedDataSet(List<PointOfInterest> pointOfInterestList);
    void dataSetChanged();
}
