package com.armjld.busaty.ActionClasses;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.armjld.busaty.R;

import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;

public class PermisionActions implements ActivityCompat.OnRequestPermissionsResultCallback {

    Context mContext;
    int CAMERA_CODE = 1001;
    int WRITE_EXTERNAL_STORAGE_CODE = 1002;
    int READ_EXTERNAL_STORAGE_CODE = 1005;
    int PHONE_CODE = 1000;
    int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public PermisionActions(Context mContext) {
        this.mContext = mContext;
    }

    public boolean GPS() {
        if(fineLocation()) {
            final LocationManager manager = (LocationManager) mContext.getSystemService( Context.LOCATION_SERVICE );
            if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER) ) { // Check if GPS is Enabled
                return true;
            } else {
                buildAlertMessageNoGps();
                return false;
            }
        }
        return false;
    }

    private void buildAlertMessageNoGps() {
        BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder((Activity) mContext).setMessage("الرجاء فتح اعدادات اللوكيشن ؟").setCancelable(true).setPositiveButton("حسنا", R.drawable.ic_tick_green, (dialogInterface, which) -> {
            mContext.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            dialogInterface.dismiss();
        }).setNegativeButton("لا", R.drawable.ic_close, (dialogInterface, which) -> dialogInterface.dismiss()).setAnimation(R.raw.location).build();
        mBottomSheetDialog.show();
    }
    
    public boolean fineLocation() {
        checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, MY_PERMISSIONS_REQUEST_LOCATION);
        return ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
    
    public boolean camPerm() {
        checkPermission(Manifest.permission.CAMERA, CAMERA_CODE);
        return ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    public boolean STORAGE_Perm() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return checkForStorageSDK30();
        }

        return readStorage() && writeStorage();
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private boolean checkForStorageSDK30() {
        if(!Environment.isExternalStorageManager()) {
            takeStoragePermSDK30();
            return false;
        }

        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void takeStoragePermSDK30() {
        try {
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setData(Uri.parse(String.format("package:%s", mContext.getApplicationContext().getPackageName())));
            mContext.startActivity(intent);
            //mContext.startActivityFor
        } catch (Exception e) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
            mContext.startActivity(intent);
        }
    }

    private boolean writeStorage() {
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE_CODE);
        return ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }


    private boolean readStorage() {
        checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE_CODE);
        return ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public boolean Phone_Perm() {
        checkPermission(Manifest.permission.CALL_PHONE, PHONE_CODE);
        return ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED;
    }

    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(mContext, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions((Activity) mContext, new String[] { permission }, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(mContext, "Camera Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
            }
        } else if(requestCode == WRITE_EXTERNAL_STORAGE_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(mContext, "WRITE Storage Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "WRITE Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        } else if(requestCode == PHONE_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(mContext, "Phone Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "Phone Permission Denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(mContext, "Fine Location Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "Fine Location Permission Denied", Toast.LENGTH_SHORT).show();
            }
        } else if(requestCode == READ_EXTERNAL_STORAGE_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(mContext, "READ Storage Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "READ Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
