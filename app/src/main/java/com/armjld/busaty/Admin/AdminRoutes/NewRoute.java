package com.armjld.busaty.Admin.AdminRoutes;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.armjld.busaty.R;
import com.armjld.busaty.Utill.Route;
import com.armjld.busaty.Utill.Validity;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import Models.Stops;
import at.markushi.ui.CircleButton;
import es.dmoral.toasty.Toasty;

public class NewRoute extends FragmentActivity implements OnMapReadyCallback {

    LinearLayout linStops;
    ViewFlipper viewFlipper;
    CircleButton btnBack, btnNext;
    GoogleMap mMap;
    EditText txtCode, txtMoney;
    Validity validity = new Validity();

    List<EditText> allEDs = new ArrayList<>();
    public static ArrayList<Stops> listStops = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_route);

        linStops = findViewById(R.id.linStops);
        viewFlipper = findViewById(R.id.viewFlipper);
        btnBack = findViewById(R.id.btnBack);
        btnNext = findViewById(R.id.btnNext);
        txtMoney = findViewById(R.id.txtMoney);
        txtCode = findViewById(R.id.txtCode);

        listStops.clear();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        btnNext.setOnClickListener(v-> {
            showNext();
        });

        btnBack.setOnClickListener(v-> {
            showPrev();
        });

        addRow();
    }

    private void showNext() {
        switch (viewFlipper.getDisplayedChild()) {
            case 0:
                if(!validity.isNumb(txtMoney.getText().toString().trim())) {
                    return;
                }

                if(txtCode.getText().toString().isEmpty()) {
                    return;
                }

                viewFlipper.showNext();
                break;
            case 1 :
                if(listStops.size() == 0) {
                    Toasty.warning(this, "Please add the Route Stops", Toasty.LENGTH_LONG, true).show();
                    return;
                }
                saveRoute();
                break;
        }
    }

    private void saveRoute() {
    }

    private void showPrev() {
        switch (viewFlipper.getDisplayedChild()) {
            case 0:
                finish();
                break;
            case 1 :
                viewFlipper.showPrevious();
                break;
        }
    }

    private void addRow() {
        EditText edText = new EditText(this);
        edText.setId(allEDs.size() + 1);
        //edText.setEnabled(false);
        edText.setFocusable(false);
        edText.setHint("محطه رقم : " + (allEDs.size() + 1));
        linStops.addView(edText);

        allEDs.add(edText);
        edText.setOnClickListener(v-> {
            ChooseStop.txtStop = edText;
            startActivity(new Intent(this, ChooseStop.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!allEDs.get(allEDs.size() - 1).getText().toString().isEmpty()) {
            addRow();
        }

        updateMap();
    }

    private void updateMap() {
        if(mMap == null) return;
        mMap.clear();

        List<Marker> markers = new ArrayList<Marker>();
        for(int i = 0; i < listStops.size(); i++) {
            Stops stops = listStops.get(i);
            LatLng latLng = new LatLng(Double.parseDouble(stops.getLat()), Double.parseDouble(stops.get_long()));
            MarkerOptions markerOptions = new MarkerOptions().position(latLng);

            Marker marker = mMap.addMarker(markerOptions);
            markers.add(marker);

            drawRoutes();
            zoomInMarkers(markers);
        }
    }

    private List<LatLng> convertToLat() {
        List<LatLng> latLangs = new ArrayList<LatLng>();
        for(int i = 0; i < listStops.size(); i++) {
            Stops stops = listStops.get(i);
            LatLng latLng = new LatLng(Double.parseDouble(stops.getLat()), Double.parseDouble(stops.get_long()));
            latLangs.add(latLng);
        }

        return latLangs;
    }


    private void drawRoutes() {
        Route route = new Route();
        route.setRoute(mMap, listStops);
    }

    private void zoomInMarkers(List<Marker> markers) {
        LatLngBounds.Builder b = new LatLngBounds.Builder();
        for (Marker m : markers) {
            b.include(m.getPosition());
        }
        LatLngBounds bounds = b.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 15,15,2);
        mMap.animateCamera(cu);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

}