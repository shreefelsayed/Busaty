package com.armjld.busaty.Admin.NewStops;

import android.Manifest;
import android.app.Dialog;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.armjld.busaty.ActionClasses.PermisionActions;
import com.armjld.busaty.ActionClasses.StopsActions;
import com.armjld.busaty.Adapters.LocationsAdapter;
import com.armjld.busaty.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import at.markushi.ui.CircleButton;
import es.dmoral.toasty.Toasty;

public class AddStop extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener, GoogleMap.OnInfoWindowClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleMap.OnMapClickListener {

    FusedLocationProviderClient fusedLocationProviderClient;
    LatLng stopLocation;
    GoogleApiClient mGoogleApiClient;

    GoogleMap mMap;
    Geocoder geocoder;

    PermisionActions permisionActions;
    LinearLayout linSearchResult;
    RecyclerView recycler;
    CircleButton btnNext, btnBack;
    EditText txtSearch;

    LocationsAdapter locationsAdapter;

    ViewFlipper viewFlipper;
    EditText txtName;
    TextInputLayout tlName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stop);

        permisionActions = new PermisionActions(this);
        geocoder = new Geocoder(this);

        viewFlipper = findViewById(R.id.viewFlipper);
        txtName = findViewById(R.id.txtName);
        tlName = findViewById(R.id.tlName);

        recycler = findViewById(R.id.recycler);
        linSearchResult = findViewById(R.id.linSearchResult);
        btnNext = findViewById(R.id.btnNext);
        btnBack = findViewById(R.id.btnBack);
        txtSearch = findViewById(R.id.txtSearch);

        recycler.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recycler.setLayoutManager(layoutManager);

        btnBack.setOnClickListener(v -> finish());

        linSearchResult.setVisibility(View.GONE);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        buildGoogleAPIClient();
        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                updateUI();
            }
        });

        txtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tlName.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        btnNext.setOnClickListener(v-> showNext());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    private void showNext() {
        switch (viewFlipper.getDisplayedChild()) {
            case 0 :
                if(stopLocation != null) {
                    viewFlipper.showNext();
                } else {
                    Toasty.warning(this, "Please choose the location", Toasty.LENGTH_LONG, true).show();
                }
                break;
            case 1 :
                if(txtName.getText().toString().trim().isEmpty()) {
                    tlName.setError("Please type the stop name");
                    return;
                }
                saveLocation();
                break;
        }
    }

    private void saveLocation() {
        StopsActions stopsActions = new StopsActions();
        stopsActions.createAStop(txtName.getText().toString().trim(), String.valueOf(stopLocation.latitude),String.valueOf(stopLocation.longitude));
        Toasty.success(this, "Stop Added Successfuly", Toasty.LENGTH_LONG, true).show();
        finish();
    }


    private void updateUI() {
        if (txtSearch.getText().toString().trim().isEmpty()) {
            linSearchResult.setVisibility(View.GONE);
            return;
        }

        ArrayList<Address> listResults = null;
        try {
            listResults = SearchForLoc(txtSearch.getText().toString().trim());
        } catch (IOException e) {
            e.printStackTrace();
        }

        locationsAdapter = new LocationsAdapter(this, listResults, mMap,stopLocation);
        recycler.setAdapter(locationsAdapter);
        linSearchResult.setVisibility(View.VISIBLE);
    }

    private ArrayList<Address> SearchForLoc(String strSearch) throws IOException {
        ArrayList<Address> address = new ArrayList<>();

        List<Address> listAd = geocoder.getFromLocationName(strSearch, 5);
        for (int i = 0; i < listAd.size(); i++) {
            Address ar = listAd.get(i);
            if (ar.getCountryName().equals("EG")) {
                address.add(ar);
            }
        }

        return address;
    }

    @Override
    protected void onResume() {
        super.onResume();
        permisionActions.GPS();
        buildGoogleAPIClient();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if (permisionActions.GPS()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                buildGoogleAPIClient();
                mMap.setMyLocationEnabled(true);
            }
        }

        mMap.setOnMapClickListener(this);
        setStyle();
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    private void setStyle() {
        //mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, 105);
            } catch (IntentSender.SendIntentException e) {
                mGoogleApiClient.connect();
            }
        } else {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 1);
            assert dialog != null;
            dialog.show();
        }
    }

    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {

    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        mMap.clear();
        stopLocation = latLng;
        mMap.addMarker(new MarkerOptions().position(latLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
    }

    private void buildGoogleAPIClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        }
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }
}