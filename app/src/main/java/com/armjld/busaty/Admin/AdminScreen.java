package com.armjld.busaty.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import com.armjld.busaty.Admin.AdminRoutes.NewRoute;
import com.armjld.busaty.Admin.NewStops.AddStop;
import com.armjld.busaty.Admin.NewStops.StopsActivity;
import com.armjld.busaty.Admin.Routes.RoutesActivity;
import com.armjld.busaty.R;

public class AdminScreen extends AppCompatActivity {

    TextView addStops, allStops, newRoute,allRoutes;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finishAffinity();
            System.exit(0);
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "اضغط مرة اخري للخروج من التطبيق", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> doubleBackToExitPressedOnce=false, 2000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_screen);

        addStops = findViewById(R.id.addStops);
        allStops = findViewById(R.id.allStops);
        newRoute = findViewById(R.id.newRoute);
        allRoutes = findViewById(R.id.allRoutes);

        allRoutes.setOnClickListener(v-> startActivity(new Intent(this, RoutesActivity.class)));

        allStops.setOnClickListener(v-> startActivity(new Intent(this, StopsActivity.class)));

        newRoute.setOnClickListener(v-> startActivity(new Intent(this, NewRoute.class)));

        addStops.setOnClickListener(v-> startActivity(new Intent(this, AddStop.class)));
    }
}