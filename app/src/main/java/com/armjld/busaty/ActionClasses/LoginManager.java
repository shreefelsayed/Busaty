package com.armjld.busaty.ActionClasses;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.armjld.busaty.Admin.AdminScreen;
import com.armjld.busaty.Driver.DriverScreen;
import com.armjld.busaty.LoginScreen;
import com.armjld.busaty.MainUser.MainScreen;
import com.armjld.busaty.Utill.UserInFormation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;

import Models.User;
import es.dmoral.toasty.Toasty;


public class LoginManager {

    Context mContext;

    public LoginManager(Context mContext) {
        this.mContext = mContext;
    }

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference uDatabase = FirebaseDatabase.getInstance().getReference().child("users");

    public static boolean dataset = false;

    public void setMyInfo() {
        mAuth = FirebaseAuth.getInstance();
        //UserActions userActions = new UserActions(mContext);

        uDatabase.child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).keepSynced(true);

        uDatabase.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    assert user != null;

                    if(user.getActive().equals("false")) {
                        Toasty.warning(mContext, "تم اغلاق حسابك، الرجاء الاتصال بالدعم الفني", Toast.LENGTH_SHORT, true).show();
                        clearInfo();
                        return;
                    }

                    setUserInformation(user);

                    setSignInValues();

                    NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancelAll();

                    //userActions.setUser(user, snapshot);

                    dataset = true;

                    //((Activity) mContext).finish();

                    switch (user.getType()) {
                        case "Driver":
                            mContext.startActivity(new Intent(mContext, DriverScreen.class));
                            break;
                        case "Admin":
                            mContext.startActivity(new Intent(mContext, AdminScreen.class));
                            break;
                        case "User" :
                            MainScreen.whichFrag = "Main";
                            mContext.startActivity(new Intent(mContext, MainScreen.class));
                            break;
                        default:
                            Toasty.warning(mContext, "لا يمكنك تسجيل الدخول", Toast.LENGTH_SHORT, true).show();
                            clearInfo();
                            break;
                    }
                } else {
                    ((Activity) mContext).finish();
                    mContext.startActivity(new Intent(mContext, LoginScreen.class));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    public void setUserInformation(User user) {
        UserInFormation.setUser(user);
    }

    private void setSignInValues() {
        // --- Set Device Name ---
        uDatabase.child(UserInFormation.getUser().getId()).child("device").setValue(getDeviceName());

        // ---- Set App Version ---
        try {
            PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            String version = pInfo.versionName;
            uDatabase.child(UserInFormation.getUser().getId()).child("app_version").setValue(version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // ------ Set IP Address ---
        new GetPublicIP().execute();
        @SuppressLint("HardwareIds") String android_id = Secure.getString(mContext.getContentResolver(), Secure.ANDROID_ID);
        uDatabase.child(UserInFormation.getUser().getId()).child("unique_id").setValue(android_id);


        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult();
                uDatabase.child(UserInFormation.getUser().getId()).child("device_token").setValue(android_id);
            }
        });
    }

    public void clearInfo() {
        uDatabase.child(UserInFormation.getUser().getId()).child("device_token").setValue("");

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        if(UserInFormation.getUser().getType().equals("Driver")) {
            HyperClass hyperClass = new HyperClass(mContext);
            if(hyperClass.isRunning()) hyperClass.stopTracking(UserInFormation.getUser(), null);
        }

        mAuth.signOut();
        UserInFormation.clearUser();
        dataset = false;

        ((Activity)mContext).finish();
        mContext.startActivity(new Intent(mContext, LoginScreen.class));
        Toasty.success(mContext, "تم تسجيل الخروج بنجاح", Toast.LENGTH_SHORT, true).show();
    }

    @SuppressLint("StaticFieldLeak")
    public class GetPublicIP extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            String publicIP = "";
            try  {
                java.util.Scanner s = new java.util.Scanner(new java.net.URL("https://api.ipify.org").openStream(), "UTF-8").useDelimiter("\\A");
                publicIP = s.next();
                System.out.println("My current IP address is " + publicIP);
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }

            return publicIP;
        }

        @Override
        protected void onPostExecute(String publicIp) {
            super.onPostExecute(publicIp);
            uDatabase.child(UserInFormation.getUser().getId()).child("ip").setValue(publicIp);
        }
    }

    /** Returns the consumer friendly device name */
    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }

        return phrase.toString();
    }
}
