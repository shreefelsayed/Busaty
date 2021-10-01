package com.armjld.busaty.Utill;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.armjld.busaty.ActionClasses.PermisionActions;
import com.armjld.busaty.ActionClasses.RouteActions;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import Models.Routes;
import at.markushi.ui.CircleButton;
import es.dmoral.toasty.Toasty;

public class GetLocation {

    LatLng prevLocation;
    PermisionActions permisionActions;
    CircleButton button;
    boolean isRecording = false;

    LocationListener mLoc;
    Routes routes;
    Context mContext;
    LocationManager locationManager;

    public boolean isRecording() {
        return isRecording;
    }

    public void setRecording(boolean recording) {
        isRecording = recording;
    }

    public GetLocation(Context mContext, Routes routes, CircleButton button) {
        this.routes = routes;
        this.mContext = mContext;
        this.button = button;
    }

    public void startRecoring() {
        permisionActions = new PermisionActions(mContext);
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        mLoc = new MyLocationListener();
        if (permisionActions.GPS()) {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLoc);
                Toasty.success(mContext, "Thanks for your Support!", Toasty.LENGTH_LONG, true).show();
                isRecording = true;
                if(button != null) button.setColor(Color.GREEN);
            }
        }
    }

    public void destroyListener() {
        isRecording = false;
        locationManager.removeUpdates(mLoc);
        if(button != null) button.setColor(Color.YELLOW);
        Toasty.success(mContext, "Your help is appreciated", Toasty.LENGTH_LONG, true).show();
    }

    private void setPoint(LatLng point) {
        if(routes == null) return;
        RouteActions routeActions = new RouteActions(mContext);
        routeActions.updateRoute(point, routes);
        //RouteActions routeActions = new RouteActions(mContext);
        //routeActions.addWayPoint(routes, point, type);
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

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(@NonNull Location location) {
            if(location == null) return;

            LatLng newLoc = new LatLng(location.getLatitude(), location.getLongitude());

            if(prevLocation == null) {
                prevLocation = newLoc;
                return;
            }

            double distance = distance(prevLocation, newLoc);

            if(distance >= 0.5) {
                prevLocation = newLoc;
                setPoint(prevLocation);
            }
        }
    }
}
