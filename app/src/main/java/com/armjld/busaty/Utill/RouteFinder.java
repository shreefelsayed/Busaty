package com.armjld.busaty.Utill;

import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import Models.Routes;

public class RouteFinder {

    public ArrayList<Routes> getNearestRoutes(ArrayList<Routes> allRoutes, LatLng from, LatLng to) {

        ArrayList<Routes> finalRoutes = new ArrayList<>();

        if(from == null || to == null) return finalRoutes;

        if(allRoutes.size() == 0) {
            Log.i("RouteFinder", "Main List is NULL !");

            return finalRoutes;
        }

        for (int i = 0; i < allRoutes.size(); i ++) {
            Routes routes = allRoutes.get(i);

            if(routes.getListPoints().size() == 0 || routes.getListStops().size() == 0) {
                Log.i("Routes Data", "Error in Routes List");
                continue;
            }

            RouteChooser routeChooser = new RouteChooser(from, to, routes);
            routeChooser.chooseDirection();

            Double pickPointDis = getDistanceToNearestPoint(from, routes.getListPoints());
            Double dropPointDis = getDistanceToNearestPoint(to, routes.getListPoints());
            routes.setDisDrop(dropPointDis);
            routes.setDisPick(pickPointDis);

            Log.i("RouteFinder", "Seted Route Data");
            finalRoutes.add(routes);
        }

        Collections.sort(finalRoutes, (c1, c2) -> Double.compare((c1.getDisDrop() + c1.getDisPick()), (c2.getDisDrop() + c2.getDisPick())));

        ArrayList<Routes> listFinal = new ArrayList<>();
        Log.i("RouteFinder", "finalRoute size : " + finalRoutes.size());

        if(finalRoutes.size() != 0) {

            for(int i = 0; i < 3; i++) {
                listFinal.add(finalRoutes.get(i));
            }
        }

        Log.i("RouteFinder", "listFinal size : " + listFinal.size());

        return listFinal;
    }

    public Double getDistanceToNearestPoint(LatLng currentLoc, List<LatLng> Route) {
        return SphericalUtil.computeDistanceBetween(currentLoc, findNearestPoint(currentLoc, Route)) / 0.62137;
        //return distance(currentLoc, findNearestPoint(currentLoc, Route));
    }

    public LatLng findNearestPoint(LatLng test, List<LatLng> target) {
        double distance = -1;
        LatLng minimumDistancePoint = test;

        if (test == null || target == null) {
            return minimumDistancePoint;
        }

        for (int i = 0; i < target.size(); i++) {
            LatLng point = target.get(i);

            int segmentPoint = i + 1;
            if (segmentPoint >= target.size()) {
                segmentPoint = 0;
            }

            double currentDistance = PolyUtil.distanceToLine(test, point, target.get(segmentPoint));
            if (distance == -1 || currentDistance < distance) {
                distance = currentDistance;
                minimumDistancePoint = findNearestPoint(test, point, target.get(segmentPoint));
            }
        }

        return minimumDistancePoint;
    }

    public LatLng findNearestPoint(final LatLng p, final LatLng start, final LatLng end) {
        if (start.equals(end)) {
            return start;
        }

        final double s0lat = Math.toRadians(p.latitude);
        final double s0lng = Math.toRadians(p.longitude);
        final double s1lat = Math.toRadians(start.latitude);
        final double s1lng = Math.toRadians(start.longitude);
        final double s2lat = Math.toRadians(end.latitude);
        final double s2lng = Math.toRadians(end.longitude);

        double s2s1lat = s2lat - s1lat;
        double s2s1lng = s2lng - s1lng;
        final double u = ((s0lat - s1lat) * s2s1lat + (s0lng - s1lng) * s2s1lng)
                / (s2s1lat * s2s1lat + s2s1lng * s2s1lng);
        if (u <= 0) {
            return start;
        }
        return end;

        //return new LatLng(start.latitude + (u * (end.latitude - start.latitude)),start.longitude + (u * (end.longitude - start.longitude)));
    }

    private double distance(LatLng from, LatLng to) {
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
