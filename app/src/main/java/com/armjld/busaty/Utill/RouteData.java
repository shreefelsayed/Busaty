package com.armjld.busaty.Utill;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.armjld.busaty.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import Models.Routes;
import Models.Stops;

public class RouteData {
    Routes routes;
    Context mContext;

    public RouteData(Routes routes, Context mContext) {
        this.routes = routes;
        this.mContext = mContext;
    }

    public void setRoute() {
        if(routes.getListStops().size() > 1) {
            String url = makeURL(routes.getListStops());
            DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute(url);
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

        urlString.append("&sensor=false&mode=driving");
        urlString.append("&key=" + mContext.getResources().getString(R.string.google_maps_key));

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
            ArrayList points;
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
                lineOptions.width(12);
                lineOptions.color(Color.parseColor("#CA4940"));
                lineOptions.geodesic(true);

            }

            if(lineOptions != null) {
                routes.setListPoints(lineOptions.getPoints());
            } else {
                Log.i("Route", "Draw Line : Line options are null");
            }
        }

        private void fitPoly(PolylineOptions lineOptions) {
            
        }

        private LatLngBounds getPolygonLatLngBounds(final List<LatLng> polygon) {
            final LatLngBounds.Builder centerBuilder = LatLngBounds.builder();
            for (LatLng point : polygon) {
                centerBuilder.include(point);
            }
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

    private BitmapDescriptor bitmapDescriptorFromVector() {
        Drawable vectorDrawable = ContextCompat.getDrawable(mContext, R.drawable.ic_bus_stop);
        assert vectorDrawable != null;
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
