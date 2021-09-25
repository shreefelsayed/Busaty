package com.armjld.busaty.Admin.Routes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.armjld.busaty.ActionClasses.RouteActions;
import com.armjld.busaty.Adapters.RouteAdapter;
import com.armjld.busaty.Adapters.StopsAdapter;
import com.armjld.busaty.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import Models.Routes;
import Models.Stops;
import at.markushi.ui.CircleButton;

public class RoutesActivity extends AppCompatActivity {

    DatabaseReference rDatabase = FirebaseDatabase.getInstance().getReference().child("routes");
    ArrayList<Routes> listRoutes = new ArrayList<>();
    ProgressDialog mDialog;

    RecyclerView recyclerView;
    LinearLayout EmptyPanel;
    CircleButton btnBack;

    EditText txtSearch;

    RouteAdapter routeAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stops);

        mDialog = new ProgressDialog(this);

        EmptyPanel = findViewById(R.id.EmptyPanel);
        recyclerView = findViewById(R.id.recycler);
        txtSearch = findViewById(R.id.txtSearch);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v-> finish());

        EmptyPanel.setVisibility(View.GONE);

        //Recycler
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(routeAdapter == null) return;
                routeAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        getRoutes();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!txtSearch.getText().toString().isEmpty()) {
            if(routeAdapter == null) return;
            routeAdapter.getFilter().filter(txtSearch.getText().toString());
        }
    }

    private void getRoutes() {
        mDialog.setMessage("Getting Routes ..");
        mDialog.setCancelable(false);
        mDialog.show();

        listRoutes.clear();

        RouteActions routeActions = new RouteActions();

        rDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for(DataSnapshot ds : snapshot.getChildren()) {
                        Routes routes = ds.getValue(Routes.class);
                        listRoutes.add(routes);

                        routeActions.setRouteData(routes, ds, routeAdapter, listRoutes.size());
                    }
                }

                updateUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void updateUI() {
        mDialog.dismiss();
        if (listRoutes.size() > 0) {
            EmptyPanel.setVisibility(View.GONE);
        } else {
            EmptyPanel.setVisibility(View.VISIBLE);
        }

        routeAdapter = new RouteAdapter(this, listRoutes);
        recyclerView.setAdapter(routeAdapter);
    }
}