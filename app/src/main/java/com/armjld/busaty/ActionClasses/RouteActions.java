package com.armjld.busaty.ActionClasses;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import Models.Routes;
import Models.Stops;

public class RouteActions {

    DatabaseReference sDatabase = FirebaseDatabase.getInstance().getReference().child("stops");

    public void setRouteData(Routes routes, DataSnapshot snapshot) {
        if(routes == null || snapshot == null) return;

        if(snapshot.child("stops").exists()) {
            routes.getListStopsCode().clear();
            routes.getListStops().clear();

            for(DataSnapshot ds : snapshot.child("stops").getChildren()) {
                String stop = Objects.requireNonNull(ds.getValue()).toString();
                routes.getListStopsCode().add(stop);
                addStop(routes, stop);
            }
        }

    }

    private void addStop(Routes routes, String stop) {
        sDatabase.child(stop).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Stops stops = snapshot.getValue(Stops.class);
                routes.getListStops().add(stops);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}
