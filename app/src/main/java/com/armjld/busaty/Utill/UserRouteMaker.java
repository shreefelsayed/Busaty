package com.armjld.busaty.Utill;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import com.armjld.busaty.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import Models.Routes;
import Models.Stops;

public class UserRouteMaker {
    GoogleMap mMap;
    Context mContext;
    Routes routes;
    LatLng pickUp;
    LatLng drop;

    RouteFinder routeFinder = new RouteFinder();
    LatLng nearestPick, nearestDrop;

    public UserRouteMaker(GoogleMap mMap, Context mContext, Routes routes, LatLng pickUp, LatLng drop) {
        this.mMap = mMap;
        this.mContext = mContext;
        this.routes = routes;
        this.pickUp = pickUp;
        this.drop = drop;
    }

    public void setRoute() {
        mMap.clear();

        if(routes.getListStops().size() > 1) {
            String url = makeURL(routes.getListStops());
            DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute(url);
        }
        setPoints();
    }

    private void routesToPick() {
        String url = makePickURL(pickUp, nearestPick);
        ActionsTask actionsTask = new ActionsTask();
        actionsTask.execute(url);
    }

    private void routesToDrop() {
        String url = makePickURL(drop, nearestDrop);
        ActionsTask actionsTask = new ActionsTask();
        actionsTask.execute(url);
    }

    private String makePickURL(LatLng from, LatLng to) {
        return "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=" +// from
                from.latitude +
                ',' +
                from.longitude +
                "&destination=" +
                to.latitude +
                ',' +
                to.longitude +
                "&sensor=false&mode=walking" +
                "&key=" + mContext.getResources().getString(R.string.google_maps_key);
    }

    private void setPoints() {
        for(int i = 0; i < routes.getListStops().size(); i ++) {
            Stops stops = routes.getListStops().get(i);
            LatLng latLng = new LatLng(Double.parseDouble(stops.getLat()), Double.parseDouble(stops.get_long()));
            MarkerOptions markerOptions = new MarkerOptions().position(latLng).icon(bitmapDescriptorFromVector(R.drawable.ic_bus_stop)).draggable(false).flat(true);

            mMap.addMarker(markerOptions);
        }
    }

    private String makeURL (ArrayList<Stops> points){
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/directions/json?");
        urlString.append("origin=");// from
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

        urlString.append("&sensor=false&mode=driving"); // mode is driving
        urlString.append("&key=").append(mContext.getResources().getString(R.string.google_maps_key));

        return urlString.toString();
    }


    @SuppressLint("StaticFieldLeak")
    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                data = downloadUrl(url[0]);
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @SuppressLint("LogNotTimber")
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(Objects.requireNonNull(point.get("lat")));
                    double lng = Double.parseDouble(Objects.requireNonNull(point.get("lng")));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }

                lineOptions.addAll(points);
            }

            if(lineOptions != null) {
                nearestPick = routeFinder.findNearestPoint(pickUp, lineOptions.getPoints());
                nearestDrop = routeFinder.findNearestPoint(drop, lineOptions.getPoints());

                setPolyLinesOnMap(lineOptions.getPoints());
                fitPoly(lineOptions);

            }
        }

        private void setPolyLinesOnMap(List<LatLng> points) {
            mMap.addPolyline(getRoutePoly(points));
            mMap.addPolyline(getToPickUp(points));
            mMap.addPolyline(getAfterDrop(points));
            routesToPick();
            routesToDrop();
            setActionPoints();
        }

        private void setActionPoints() {
            mMap.addMarker(new MarkerOptions().position(pickUp).icon(bitmapDescriptorFromVector(R.drawable.ic_my_location)).draggable(true)).setTag("pick");
            mMap.addMarker(new MarkerOptions().position(drop).icon(bitmapDescriptorFromVector(R.drawable.ic_pin)).draggable(true)).setTag("drop");

            mMap.addMarker(new MarkerOptions().position(nearestPick).icon(bitmapDescriptorFromVector(R.drawable.ic_placeholder)).draggable(false).flat(true));
            mMap.addMarker(new MarkerOptions().position(nearestDrop).icon(bitmapDescriptorFromVector(R.drawable.ic_placeholder)).draggable(false).flat(true));
        }

        private int[] getIndexes(List<LatLng> points) {

            int posPick = 0;
            int posDrop = 1;

            for(int i = 0; i < points.size(); i ++) {
                if(points.get(i) == nearestDrop) {
                    posDrop = i;
                }

                if(points.get(i) == nearestPick) {
                    posPick = i;
                }
            }
            return new int[]{posPick, posDrop};
        }

        private PolylineOptions getToPickUp(List<LatLng> points) {
            PolylineOptions lineOptions = new PolylineOptions();

            ArrayList<LatLng> finalPoint = new ArrayList<>(points.subList(0, getIndexes(points)[0]));

            lineOptions.addAll(finalPoint);
            lineOptions.width(12);
            lineOptions.color(Color.parseColor("#000000"));
            lineOptions.geodesic(true);
            return lineOptions;
        }

        private PolylineOptions getRoutePoly(List<LatLng> points) {
            PolylineOptions lineOptions = new PolylineOptions();
            ArrayList<LatLng> finalPoint = new ArrayList<>(points.subList(getIndexes(points)[0], getIndexes(points)[1]));

            lineOptions.addAll(finalPoint);
            lineOptions.width(12);
            lineOptions.color(Color.parseColor("#CA4940"));
            lineOptions.geodesic(true);
            return lineOptions;
        }

        private PolylineOptions getAfterDrop(List<LatLng> points) {
            PolylineOptions lineOptions = new PolylineOptions();
            ArrayList<LatLng> finalPoint = new ArrayList<>(points.subList(getIndexes(points)[1], points.size() - 1));

            lineOptions.addAll(finalPoint);
            lineOptions.width(12);
            lineOptions.color(Color.parseColor("#000000"));
            lineOptions.geodesic(true);
            return lineOptions;
        }

        private void fitPoly(PolylineOptions lineOptions) {
            final int POLYGON_PADDING_PREFERENCE = 150;
            //final LatLngBounds latLngBounds = getPolygonLatLngBounds(lineOptions.getPoints());

            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(getPickUpBounds(), POLYGON_PADDING_PREFERENCE));
        }

        private LatLngBounds getPolygonLatLngBounds(final List<LatLng> polygon) {
            final LatLngBounds.Builder centerBuilder = LatLngBounds.builder();
            for (LatLng point : polygon) {
                centerBuilder.include(point);
            }
            return centerBuilder.build();
        }

        private LatLngBounds getPickUpBounds() {
            final LatLngBounds.Builder centerBuilder = LatLngBounds.builder();
            centerBuilder.include(pickUp);
            centerBuilder.include(nearestPick);
            return centerBuilder.build();
        }
    }

    @SuppressLint("LogNotTimber")
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();
            Log.i("Route", "Route Data : " + data);

        } catch (Exception e) {
            Log.i("Route", "Route Error : " + e.toString());
        } finally {
            assert iStream != null;
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    @SuppressLint("StaticFieldLeak")
    private class ActionsTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                data = downloadUrl(url[0]);
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ActionsParser parserTask = new ActionsParser();
            parserTask.execute(result);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class ActionsParser extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @SuppressLint("LogNotTimber")
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(Objects.requireNonNull(point.get("lat")));
                    double lng = Double.parseDouble(Objects.requireNonNull(point.get("lng")));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }

                lineOptions.addAll(points);
            }

            if (lineOptions != null) {
                lineOptions.width(8);
                lineOptions.color(Color.parseColor("#000000"));
                List<PatternItem> patternItems = Arrays.asList(new Dot(), new Gap(20));
                mMap.addPolyline(lineOptions).setPattern(patternItems);
            }
        }
    }

    private BitmapDescriptor bitmapDescriptorFromVector(@DrawableRes int id) {
        Drawable vectorDrawable = ContextCompat.getDrawable(mContext, id);
        assert vectorDrawable != null;
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
