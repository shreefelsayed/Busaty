package com.armjld.busaty.Utill;

import android.annotation.SuppressLint;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.Collections;

import Models.Routes;
import Models.Stops;

public class RouteChooser {

    LatLng pickUp;
    LatLng drop;
    Routes routes;
    String TAG = "Route Chooser";

    public RouteChooser(LatLng pickUp, LatLng drop, Routes routes) {
        this.pickUp = pickUp;
        this.drop = drop;
        this.routes = routes;
    }

    @SuppressLint("LogNotTimber")
    public String chooseDirection() {
        Stops stopsPick = routes.getListStops().get(0);
        Stops stopsDrop = routes.getListStops().get(0);

        Double pickDis = getDis(pickUp, new LatLng(Double.parseDouble(routes.getListStops().get(0).getLat()), Double.parseDouble(routes.getListStops().get(0).get_long())));
        Double dropDis = getDis(drop, new LatLng(Double.parseDouble(routes.getListStops().get(0).getLat()), Double.parseDouble(routes.getListStops().get(0).get_long())));

        int indexPick = 0;
        int indexDrop = 0;

        for(int i = 0; i < routes.getListStops().size(); i ++) {
            Double distancePick = getDis(pickUp, new LatLng(Double.parseDouble(routes.getListStops().get(i).getLat()), Double.parseDouble(routes.getListStops().get(i).get_long())));
            Double distanceDrop = getDis(drop, new LatLng(Double.parseDouble(routes.getListStops().get(i).getLat()), Double.parseDouble(routes.getListStops().get(i).get_long())));

            if(distancePick < pickDis) {
                indexPick = i;
                stopsPick = routes.getListStops().get(i);
            }

            if(distanceDrop < dropDis) {
                indexDrop = i;
                stopsDrop = routes.getListStops().get(i);
            }
        }

        Log.i(TAG, "Nearest PickUp : " + stopsPick.getName() + " Nearest Drop : " + stopsDrop.getName());

        if(indexPick > indexDrop) {
            Collections.reverse(routes.getListStops());
            Log.i(TAG, "List Reversed");
            return "reverse";
        }

        Log.i(TAG, "Shouldn't Reverse");
        return "normal";
    }

    private Double getDis(LatLng from, LatLng to) {
        double lat1 = from.latitude;
        double lon1 = from.longitude;
        double lat2 = to.latitude;
        double lon2 = to.longitude;
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515 / 0.62137;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}
