package com.armjld.busaty.MainUser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;
import com.armjld.busaty.ActionClasses.LoginManager;
import com.armjld.busaty.R;
import com.armjld.busaty.SplashScreen;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class MainScreen extends AppCompatActivity {

    ChipNavigationBar navBar;
    public static String whichFrag = "Main";
    boolean doubleBackToExitPressedOnce = false;

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

        navBar = findViewById(R.id.navBar);


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

    private Fragment whichFrag() {
        Fragment frag = null;
        switch (whichFrag) {
            case "Main":
                frag = new Main();
                //navBar.setSelected(R.id.home , false);
                break;
            case "Rides":
                frag = new Rides();
                //navBar.setSelected(R.id.rides , false);
                break;
            case "Settings":
                frag = new Settings();
                //navBar.setSelected(R.id.settings , false);
                break;
        }

        assert frag != null;
        getSupportFragmentManager().beginTransaction().replace(R.id.container, frag, whichFrag).addToBackStack("Main").commit();
        return frag;
    }
}