package com.armjld.busaty.MainUser;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.armjld.busaty.ActionClasses.PermisionActions;
import com.armjld.busaty.Adapters.RouteAdapter;
import com.armjld.busaty.Admin.AdminRoutes.NewRoute;
import com.armjld.busaty.R;
import com.armjld.busaty.Utill.RouteFinder;
import com.armjld.busaty.Utill.RouteMaker;
import com.armjld.busaty.Utill.UserRouteMaker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

import Models.Routes;
import Models.Stops;
import es.dmoral.toasty.Toasty;

public class Main extends Fragment implements OnMapReadyCallback, View.OnClickListener, GoogleMap.OnInfoWindowClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleMap.OnMapClickListener, RouteAdapter.AdapterCallback {

    public Main() { }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    GoogleMap mMap;
    FusedLocationProviderClient fusedLocationProviderClient;
    GoogleApiClient mGoogleApiClient;
    Geocoder geocoder;
    AutocompleteSupportFragment pickUpAutoComplete, dropAutoComplete;
    Context mContext;

    LatLng myLocation, dropLocation;

    PermisionActions permisionActions;

    RouteFinder routeFinder = new RouteFinder();
    RecyclerView recycler;
    RouteAdapter routeAdapter;

    @SuppressLint("PotentialBehaviorOverride")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        recycler = view.findViewById(R.id.recycler);
        pickUpAutoComplete = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.pickUpAutoComplete);
        dropAutoComplete = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.dropAutoComplete);
        pickUpAutoComplete.getView().setBackgroundResource(R.drawable.layout_rounded_corner);
        dropAutoComplete.getView().setBackgroundResource(R.drawable.layout_rounded_corner);

        recycler.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setReverseLayout(false);
        layoutManager.setStackFromEnd(false);
        recycler.setLayoutManager(layoutManager);

        intalizePlaces();
        setListners();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);

        buildGoogleAPIClient();

        final LocationManager manager2 = (LocationManager) mContext.getSystemService( Context.LOCATION_SERVICE );
        if (manager2.isProviderEnabled(LocationManager.GPS_PROVIDER) ) {
            fetchLocation();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        fetchLocation();
        return view;
    }

    private void setListners() {
        dropAutoComplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                dropLocation = place.getLatLng();
                updateUI(routeFinder.getNearestRoutes(MainScreen.listRoutes, myLocation, dropLocation));
            }

            @Override
            public void onError(@NonNull Status status) { }
        });

        pickUpAutoComplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                myLocation = place.getLatLng();
                updateUI(routeFinder.getNearestRoutes(MainScreen.listRoutes, myLocation, dropLocation));
            }

            @Override
            public void onError(@NonNull Status status) { }
        });
    }

    private void updateUI(ArrayList<Routes> nearestRoutes) {
        if(nearestRoutes.size() > 0) {
            UserRouteMaker userRouteMaker = new UserRouteMaker(mMap, mContext, nearestRoutes.get(0), myLocation, dropLocation);
            userRouteMaker.setRoute();

            if(nearestRoutes.size() > 1) {
                routeAdapter = new RouteAdapter(mContext, nearestRoutes, this);
                recycler.setVisibility(View.VISIBLE);
                recycler.setAdapter(routeAdapter);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        permisionActions.GPS();
        buildGoogleAPIClient();
    }

    private void intalizePlaces() {
        if (!Places.isInitialized()) {
            Places.initialize(mContext, getString(R.string.google_maps_key), Locale.US);
        }

        dropAutoComplete.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        pickUpAutoComplete.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        pickUpAutoComplete.setHint("محطه الركوب");
        dropAutoComplete.setHint("محطه الوصول");


        dropAutoComplete.setCountry("EG");
        pickUpAutoComplete.setCountry("EG");

        permisionActions = new PermisionActions(mContext);
        geocoder = new Geocoder(mContext);
    }

    @SuppressLint("PotentialBehaviorOverride")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);

        if (permisionActions.GPS()) {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                buildGoogleAPIClient();
                fetchLocation();
            }
        }

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(@NonNull Marker marker) { }

            @Override
            public void onMarkerDrag(@NonNull Marker marker) { }

            @Override
            public void onMarkerDragEnd(@NonNull Marker marker) {
                if(marker.getTag() == null) return;

                if(marker.getTag().equals("pick")) {
                    myLocation = marker.getPosition();
                } else if(marker.getTag().equals("drop")) {
                    dropLocation = marker.getPosition();
                }

                updateUI(routeFinder.getNearestRoutes(MainScreen.listRoutes, myLocation, dropLocation));
            }
        });
    }

    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if(fusedLocationProviderClient == null) {
            return;
        }

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if(location != null) {
                myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 10));
            }
        });
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        myLocation = new LatLng(location.getLatitude(), location.getLongitude());
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

    private void buildGoogleAPIClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(mContext).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        }
    }

    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult((Activity) mContext, 105);
            } catch (IntentSender.SendIntentException e) {
                mGoogleApiClient.connect();
            }
        } else {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), (Activity) mContext, 1);
            assert dialog != null;
            dialog.show();
        }
    }

    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {

    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    @Override
    public void onItemClicked(Routes routes) {
        ArrayList<Routes> list = new ArrayList<>();
        list.add(routes);
        updateUI(list);
    }
}