package com.armjld.busaty;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.armjld.busaty.ActionClasses.LoginManager;
import com.armjld.busaty.ActionClasses.PermisionActions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.Timer;
import java.util.TimerTask;

import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import es.dmoral.toasty.Toasty;

public class SplashScreen extends AppCompatActivity {

    SharedPreferences sharedPreferences = null;
    int codee = 10001;
    static DatabaseReference uDatabase;
    boolean doubleBackToExitPressedOnce = false;
    public static double minVersion;
    public static double lastVersion;

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
        setContentView(R.layout.activity_splash_screen);


        uDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        PackageInfo pInfo = null;
        try {
            pInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        assert pInfo != null;
        String version = pInfo.versionName;
        double dVersion = Double.parseDouble(version);

        //permisionActions.STORAGE_Perm();

        // -------------- Check for Updates --------------- //
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(1200).build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                minVersion = mFirebaseRemoteConfig.getDouble("min_version_main");
                lastVersion = mFirebaseRemoteConfig.getDouble("last_version_main");
                sharedPreferences = getSharedPreferences("com.armjld.busaty", MODE_PRIVATE);

                if(dVersion >= minVersion) {
                    if(dVersion >= lastVersion) {
                        whatToDo();
                    } else {
                        MaterialDialog materialDialog = new MaterialDialog.Builder(SplashScreen.this).setTitle("يوجد تحديث جديد").setMessage("هل ترغب في التحديث الأن ؟").setCancelable(false).setPositiveButton("تحديث الان", (dialogInterface, which) -> {
                            openWebURL("https://play.google.com/store/apps/details?id=com.armjld.busaty");
                            dialogInterface.dismiss();
                            finish();
                        }).setNegativeButton("لاحقا", (dialogInterface, which) -> {
                            dialogInterface.dismiss();
                            whatToDo();
                        }).build();
                        materialDialog.show();
                    }
                } else {
                    MaterialDialog materialDialog = new MaterialDialog.Builder(SplashScreen.this).setTitle("يوجد تحديث جديد").setMessage("متوفر الأن تحديث جديد للبرنامج, يجب تحديث البرنامج للاستمرار").setCancelable(false).setPositiveButton("تحديث", (dialogInterface, which) -> {
                        openWebURL("https://play.google.com/store/apps/details?id=com.armjld.busaty");
                        dialogInterface.dismiss();
                        finish();
                    }).build();
                    materialDialog.show();
                }
            }
        });
    }

    public void openWebURL(String inURL) {
        Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse(inURL) );
        startActivity(browse);
    }

    private void whatToDo() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                sharedPreferences = getSharedPreferences("com.armjld.busaty", MODE_PRIVATE);
                if(FirebaseAuth.getInstance().getCurrentUser() != null) {
                    LoginManager _lgnMn = new LoginManager(SplashScreen.this);
                    _lgnMn.setMyInfo();
                    FirebaseCrashlytics.getInstance().setUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());
                } else {
                    if(sharedPreferences.getBoolean("firstrun", true)) {
                        sharedPreferences.edit().putBoolean("firstrun", false).apply();
                        finish();
                        startActivity(new Intent(SplashScreen.this, LoginScreen.class));
                    } else {
                        finish();
                        startActivity(new Intent(SplashScreen.this, LoginScreen.class));
                    }
                }
            }
        }, 2500);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == codee) {
            if (resultCode != RESULT_OK) {
                whatToDo();
                Toasty.error(this, "لم يتم تحديث التطبيق", Toast.LENGTH_SHORT, true).show();
            }
        }
    }
}