package com.armjld.busaty.ActionClasses;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.armjld.busaty.Adapters.RouteAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

import Models.Routes;
import Models.Stops;

public class RouteActions {

    DatabaseReference sDatabase = FirebaseDatabase.getInstance().getReference().child("stops");
    DatabaseReference rDatabase = FirebaseDatabase.getInstance().getReference().child("routes");

    public void addRoute(Routes routes, ArrayList<Stops> listStops) {
        rDatabase.child(routes.getCode()).setValue(routes);
        for(int i = 0; i < listStops.size(); i ++) {
            rDatabase.child(routes.getCode()).child("stops").child((i + 1) + "").setValue(listStops.get(i).getCode());
            routes.getListStopsCode().add(listStops.get(i).getCode());
        }

        routes.setListStops(listStops);
    }

    public void setRouteData(Routes routes, DataSnapshot snapshot, RouteAdapter routeAdapter, int pos) {
        if(snapshot.child("stops").exists()) {
            routes.getListStopsCode().clear();
            routes.getListStops().clear();

            for(DataSnapshot ds : snapshot.child("stops").getChildren()) {
                String stop = Objects.requireNonNull(ds.getValue()).toString();
                routes.getListStopsCode().add(stop);
                addStop(routes, stop, ds.getKey(), routeAdapter, pos);
            }
        }

        if(routeAdapter != null) routeAdapter.notifyItemChanged(pos);
    }

    private void addStop(Routes routes, String stop, String key, RouteAdapter routeAdapter, int pos) {
        sDatabase.child(stop).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Stops stops = snapshot.getValue(Stops.class);
                assert stops != null;

                stops.setNumb(Integer.parseInt(key));
                routes.getListStops().add(stops);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Collections.sort(routes.getListStops(), Comparator.comparingInt(Stops::getNumb));
                }

                if(routeAdapter != null) routeAdapter.notifyItemChanged(pos);
                Log.i("Routes Actions", "Routes Actions : Updated");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}
