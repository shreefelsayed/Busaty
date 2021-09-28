package com.armjld.busaty.MainUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;
import com.armjld.busaty.ActionClasses.LoginManager;
import com.armjld.busaty.ActionClasses.RouteActions;
import com.armjld.busaty.R;
import com.armjld.busaty.SplashScreen;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.ArrayList;

import Models.Routes;

public class MainScreen extends AppCompatActivity {

    ChipNavigationBar navBar;
    public static String whichFrag = "Main";
    boolean doubleBackToExitPressedOnce = false;
    public static ArrayList<Routes> listRoutes = new ArrayList<>();
    ProgressDialog mDialog;

    DatabaseReference rDatabase = FirebaseDatabase.getInstance().getReference().child("routes");

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("Main");
        if (fragment != null && fragment.isVisible()) {
            if (doubleBackToExitPressedOnce) {
                finishAffinity();
                System.exit(0);
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "اضغط مرة اخري للخروج من التطبيق", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        } else {
            whichFrag = "Main";
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new Main(), whichFrag).addToBackStack("Main").commit();
            navBar.setItemSelected(R.id.home, true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!LoginManager.dataset) {
            finish();
            startActivity(new Intent(this, SplashScreen.class));
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        mDialog = new ProgressDialog(this);
        navBar = findViewById(R.id.navBar);


        getRoutes();
        navBar.setOnItemSelectedListener(i -> {
            switch (i) {
                case R.id.home :
                    whichFrag = "Main";
                    whichFrag();
                    break;
                case R.id.rides:
                    whichFrag = "Rides";
                    whichFrag();
                    break;
                case R.id.settings:
                    whichFrag = "Settings";
                    whichFrag();
                    break;
            }
        });


        getSupportFragmentManager().beginTransaction().replace(R.id.container, whichFrag(), whichFrag).addToBackStack("Main").commit();
    }

    private void getRoutes() {
        mDialog.setMessage("Loading ..");
        mDialog.setCancelable(false);
        mDialog.show();

        listRoutes.clear();
        rDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for(DataSnapshot ds : snapshot.getChildren()) {
                        Routes routes = ds.getValue(Routes.class);
                        RouteActions routeActions = new RouteActions(MainScreen.this);
                        routeActions.setRouteData(routes, ds, null, 0);

                        listRoutes.add(routes);
                    }
                }

                mDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private Fragment whichFrag() {
        Fragment frag = null;
        switch (whichFrag) {
            case "Main":
                frag = new Main();
                break;
            case "Rides":
                frag = new Rides();
                break;
            case "Settings":
                frag = new Settings();
                break;
        }

        assert frag != null;
        getSupportFragmentManager().beginTransaction().replace(R.id.container, frag, whichFrag).addToBackStack("Main").commit();
        return frag;
    }
}