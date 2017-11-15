package com.dredhat.lhadj.mapmeapi;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by lhadj on 12/29/2016.
 */

public class Point {
    private LatLng point;

    public Point(LatLng point) {
        this.point = point;
    }

    public LatLng getPoint() {
        return point;
    }

    public void setPoint(LatLng point) {
        this.point = point;
    }
}
