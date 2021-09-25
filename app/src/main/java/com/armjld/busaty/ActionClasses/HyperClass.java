package com.armjld.busaty.ActionClasses;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.widget.ImageView;
import android.widget.Toast;

import com.armjld.busaty.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hypertrack.sdk.HyperTrack;
import com.hypertrack.sdk.TrackingError;
import com.hypertrack.sdk.TrackingStateObserver;

import java.util.HashMap;
import java.util.Map;

import Models.User;
import es.dmoral.toasty.Toasty;

public class HyperClass implements TrackingStateObserver.OnTrackingStateChangeListener{

    Context mContext;
    HyperTrack sdkInstance;
    String htID = String.valueOf(R.string.hypertrack);
    DatabaseReference uDatabase = FirebaseDatabase.getInstance().getReference().child("users");


    public HyperClass(Context mContext) {
        this.mContext = mContext;
        sdkInstance = HyperTrack.getInstance(htID);
        sdkInstance.requestPermissionsIfNecessary();
    }

    public String getHtID() {
        return htID;
    }

    public void Intalize() {
        sdkInstance = HyperTrack.getInstance(htID);
    }

    public boolean isRunning() {
        if(sdkInstance == null) Intalize();

        return sdkInstance.isRunning();
    }

    public void startTracking(User user, ImageView btnTrack) {
        if(sdkInstance == null) Intalize();
        PermisionActions permisionActions = new PermisionActions(mContext);
        if(!permisionActions.GPS()) return;

        HyperTrack.enableDebugLogging();
        sdkInstance.addTrackingListener(this);

        sdkInstance.setDeviceName(user.getName());
        Map<String, Object> myMetadata = new HashMap<>();

        uDatabase.child(user.getId()).child("trackId").setValue(sdkInstance.getDeviceID());
        user.setTrackId(sdkInstance.getDeviceID());

        sdkInstance.setDeviceMetadata(myMetadata);
        sdkInstance.start();
        if(btnTrack != null) btnTrack.setImageTintList(ColorStateList.valueOf(Color.GREEN));
        Toasty.success(mContext, "تم تفعيل التتبع", Toast.LENGTH_SHORT, true).show();
    }

    public void stopTracking(User user, ImageView btnTrack) {
        if(sdkInstance == null) Intalize();

        uDatabase.child(user.getId()).child("trackId").setValue("");
        user.setTrackId("");

        sdkInstance.stop();
        sdkInstance.removeTrackingListener(this);
        if(btnTrack != null) btnTrack.setImageTintList(ColorStateList.valueOf(Color.RED));
        Toasty.warning(mContext, "تم ايقاف التتبع", Toast.LENGTH_SHORT, true).show();
    }

    public void destory() {
        sdkInstance.removeTrackingListener(this);
    }


    @Override
    public void onError(TrackingError trackingError) {

    }

    @Override
    public void onTrackingStart() {

    }

    @Override
    public void onTrackingStop() {

    }
}
