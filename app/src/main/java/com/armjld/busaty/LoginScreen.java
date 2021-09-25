package com.armjld.busaty;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import com.armjld.busaty.Admin.NewStops.AddStop;
import com.armjld.busaty.Utill.Validity;
import com.google.android.material.textfield.TextInputLayout;

import at.markushi.ui.CircleButton;

public class LoginScreen extends AppCompatActivity {

    boolean doubleBackToExitPressedOnce = false;
    ProgressDialog mDialog;

    EditText txtPhone;
    TextInputLayout tlPhone;
    CircleButton btnNext, btnGoogle, btnFacebook;

    Validity validity = new Validity();


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
        setContentView(R.layout.activity_login_screen);

        mDialog = new ProgressDialog(this);

        txtPhone = findViewById(R.id.txtPhone);
        tlPhone = findViewById(R.id.tlPhone);
        btnNext = findViewById(R.id.btnNext);
        btnGoogle  = findViewById(R.id.btnGoogle);
        btnFacebook = findViewById(R.id.btnFacebook);

        String uMail = getIntent().getStringExtra("umail");
        String uPass = getIntent().getStringExtra("upass");

        btnGoogle.setOnClickListener(v-> {
            startActivity(new Intent(this, AddStop.class));
        });

        txtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                tlPhone.setErrorEnabled(false);
            }
        });

        btnNext.setOnClickListener(v -> {
            String strPhone = txtPhone.getText().toString().trim();
            if(validity.isPhone(strPhone)) {
                openActivity(strPhone);
            } else {
                tlPhone.setError("الرجاء التأكد من رقم الهاتف");
            }
        });
    }

    private void openActivity(String strPhone) {
        ReciveOTPScreen.phone = strPhone;
        startActivity(new Intent(this, ReciveOTPScreen.class));
    }

}