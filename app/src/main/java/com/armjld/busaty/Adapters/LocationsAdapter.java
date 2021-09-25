package com.armjld.busaty.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.armjld.busaty.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;


public class LocationsAdapter extends RecyclerView.Adapter<LocationsAdapter.MyViewHolder> {
    Context mContext;
    GoogleMap mMap;
    ArrayList<Address> locations;
    LatLng stopLoc;
    
    public LocationsAdapter(Context mContext, ArrayList<Address> locations, GoogleMap mMap, LatLng stopLoc) {
        this.mContext = mContext;
        this.mMap = mMap;
        this.locations = locations;
        this.stopLoc = stopLoc;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.card_address, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "NonConstantResourceId"})
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Address address = locations.get(position);

        holder.setData(address);

        holder.myview.setOnClickListener(v-> {
            if(mMap != null) {
                mMap.clear();
                stopLoc = new LatLng(address.getLatitude(), address.getLongitude());
                mMap.addMarker(new MarkerOptions().position(new LatLng(address.getLatitude(), address.getLongitude())));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(address.getLatitude(), address.getLongitude())));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
            }
        });
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public View myview;
        TextView txtTitle, txtDisc;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            myview = itemView;
            txtTitle = myview.findViewById(R.id.txtTitle);
            txtDisc = myview.findViewById(R.id.txtDisc);
        }

        public void setData(Address address) {
            txtTitle.setText(address.getFeatureName());
            txtDisc.setText(address.getPremises() + "," + address.getLocality() + " - " + address.getCountryCode());
        }
    }
}