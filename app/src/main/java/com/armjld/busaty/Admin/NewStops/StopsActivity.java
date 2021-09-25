package com.armjld.busaty.Admin.NewStops;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.armjld.busaty.Adapters.StopsAdapter;
import com.armjld.busaty.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import Models.Stops;
import at.markushi.ui.CircleButton;

public class StopsActivity extends AppCompatActivity implements StopsAdapter.AdapterCallback {

    DatabaseReference sDatabase = FirebaseDatabase.getInstance().getReference().child("stops");
    ArrayList<Stops> listStops = new ArrayList<>();
    ProgressDialog mDialog;

    RecyclerView recyclerView;
    LinearLayout EmptyPanel;
    CircleButton btnBack;

    EditText txtSearch;

    StopsAdapter stopsAdatper;
    public static boolean isAsign = false;

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
                if(stopsAdatper == null) return;
                stopsAdatper.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        getStops();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!txtSearch.getText().toString().isEmpty()) {
            if(stopsAdatper == null) return;
            stopsAdatper.getFilter().filter(txtSearch.getText().toString());
        }
    }

    private void getStops() {
        mDialog.setMessage("Getting Stops ..");
        mDialog.setCancelable(false);
        mDialog.show();

        listStops.clear();

        sDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()) {
                    Stops stops = ds.getValue(Stops.class);
                    listStops.add(stops);

                    updateUI();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void updateUI() {
        mDialog.dismiss();
        if (listStops.size() > 0) {
            EmptyPanel.setVisibility(View.GONE);
        } else {
            EmptyPanel.setVisibility(View.VISIBLE);
        }

        stopsAdatper = new StopsAdapter(this, listStops, this);
        recyclerView.setAdapter(stopsAdatper);
    }

    @Override
    public void onItemClicked(Stops stops) {
        if(isAsign) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("result",stops.getCode());
            setResult(Activity.RESULT_OK,returnIntent);
            finish();
        }
    }
}