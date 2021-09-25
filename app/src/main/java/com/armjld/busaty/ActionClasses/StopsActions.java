package com.armjld.busaty.ActionClasses;

import com.armjld.busaty.Utill.UserInFormation;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import Models.Stops;

public class StopsActions {

    DatabaseReference sDatabase = FirebaseDatabase.getInstance().getReference().child("stops");

    public void createAStop(String name, String lat, String _long) {
        String code = genrateKey();
        Stops stop = new Stops(code,
                name,
                lat,
                _long,
                getDate(),
                UserInFormation.getUser().getId());

        sDatabase.child(code).setValue(stop);
    }

    private String getDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
        return sdf.format(new Date());
    }

    private String genrateKey() {
        int randomPIN = (int)(Math.random()*23430)+1000;
        String full = String.valueOf(randomPIN);
        return full.substring(Math.max(full.length() - 4, 0));
    }
}
