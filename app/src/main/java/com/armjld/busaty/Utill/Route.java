package com.armjld.busaty.Utill;

// -- This class is to draw a route on Google Maps;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Models.Stops;

public class Route {

    public void setRoute(GoogleMap mMap, ArrayList<Stops> listStops) {
        if(listStops.size() > 1) {
            String url = makeURL(listStops);
            drawPath(url, mMap);
        }
    }

    @SuppressLint("LogNotTimber")
    public void drawPath(String result, GoogleMap mMap) {
        try {
            //Tranform the string into a json object
            Log.i("Routes", "Route Link : " + result);
            JSONObject json = new JSONObject("{" + result + "}");
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);

            mMap.addPolyline(new PolylineOptions()
                    .addAll(list)
                    .width(12)
                    .color(Color.parseColor("#05b1fb"))//Google maps blue color
                    .geodesic(true)
            );

        } catch (JSONException e) {
            Log.i("Route", "Error in Route : " + e.toString());
        }
    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    private String makeURL (ArrayList<Stops> points){
        StringBuilder urlString = new StringBuilder();


        urlString.append("https://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(points.get(0).getLat());
        urlString.append(',');
        urlString.append(points.get(0).get_long());
        urlString.append("&destination=");
        urlString.append(points.get(points.size()-1).getLat());
        urlString.append(',');
        urlString.append(points.get(points.size()-1).get_long());

        // -- Add Way Points
        if(points.size() > 2) {
            urlString.append("&waypoints=");
            urlString.append("optimize:true|");
            urlString.append( points.get(1).getLat());
            urlString.append(',');
            urlString.append(points.get(1).get_long());

            for(int i=2;i<points.size()-1;i++) {
                urlString.append('|');
                urlString.append( points.get(i).getLat());
                urlString.append(',');
                urlString.append(points.get(i).get_long());
            }
        }

        urlString.append("&sensor=false&mode=driving");
        urlString.append("&key=AIzaSyABltNgVNN4rCCXXGDoBIZE20D91Ry9XDI");


        return urlString.toString();
    }
}
