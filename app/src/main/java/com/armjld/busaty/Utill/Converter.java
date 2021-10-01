package com.armjld.busaty.Utill;


import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import Models.Stops;

public class Converter {
    public ArrayList<LatLng> convert(ArrayList<Stops> listStops) {
        ArrayList<LatLng> listLats = new ArrayList<>();
        for(int i = 0; i < listStops.size(); i ++) {
            Stops stops = listStops.get(i);
            LatLng latLng = new LatLng(Double.parseDouble(stops.getLat()), Double.parseDouble(stops.get_long()));
            listLats.add(latLng);
        }
        return listLats;
    }
}
