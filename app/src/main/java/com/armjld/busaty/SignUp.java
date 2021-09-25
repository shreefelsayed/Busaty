package com.armjld.busaty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.armjld.busaty.ActionClasses.LoginManager;
import com.armjld.busaty.Utill.Validity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import Models.User;
import at.markushi.ui.CircleButton;
import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;
import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import es.dmoral.toasty.Toasty;

public class SignUp extends AppCompatActivity {

    ViewFlipper viewFlipper;
    EditText txtFirstName, txtLastName;
    TextInputLayout tlFirstName, tlLastName;

    EditText txtEmail;
    TextInputLayout tlEmail;
    CircleButton btnNext, btnBack;

    Validity validity = new Validity();

    public static AuthCredential authCredential;
    public static String phone = "";

    DatabaseReference uDatabase = FirebaseDatabase.getInstance().getReference().child("users");
    ProgressDialog mDialog;

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mDialog = new ProgressDialog(this);

        viewFlipper = findViewById(R.id.viewFlipper);
        txtFirstName = findViewById(R.id.txtFirstName);
        txtLastName = findViewById(R.id.txtLastName);
        tlFirstName = findViewById(R.id.tlFirstName);
        tlLastName = findViewById(R.id.tlLastName);
        txtEmail = findViewById(R.id.txtEmail);
        tlEmail = findViewById(R.id.tlEmail);
        btnNext = findViewById(R.id.btnNext);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v-> {
            BottomSheetMaterialDialog bottomSheetMaterialDialog = new BottomSheetMaterialDialog.Builder(SignUp.this).setTitle("الغاء التسجيل").setMessage("هل تريد الغاء التسجيل ؟").setCancelable(true).setPositiveButton("نعم", (dialogInterface, which) -> {
                dialogInterface.dismiss();
                finish();
            }).setNegativeButton("لا", (dialogInterface, which) -> dialogInterface.dismiss()).build();
            bottomSheetMaterialDialog.show();
        });

        btnNext.setOnClickListener(v-> showNext());
    }

    private void showNext() {
        switch (viewFlipper.getDisplayedChild()) {
            case 0 :
                if(checkName()) {
                    viewFlipper.showNext();
                }
                break;
            case 1 :
                if(checkEmail()) {
                    initSigning();
                }
                break;
        }
    }

    private void initSigning() {
        if(authCredential == null || phone.equals("")) {
            finish();
            return;
        }

        FirebaseAuth.getInstance().signInWithCredential(authCredential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                setData();
            } else {
                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                    Toasty.error(this, task.getException().toString(), Toasty.LENGTH_LONG, true).show();
                }
            }
        });
    }

    private void setData() {
        String name = txtFirstName.getText().toString().trim() + " " + txtLastName.getText().toString().trim();
        String strEmail = txtEmail.getText().toString().trim();
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        User user = new User(name, "", phone, strEmail, id, "", "User", getDate());
        uDatabase.child(id).setValue(user);

        Login();
    }

    private void Login() {
        LoginManager loginManager = new LoginManager(this);
        loginManager.setMyInfo();
    }

    private boolean checkEmail() {
        String strEmail = txtEmail.getText().toString().trim();
        if(!validity.isEmail(strEmail)) {
            tlEmail.setError("تأكد من بريدك الالكتروني");
            return false;
        }

        return true;
    }

    private boolean checkName() {
        String strF = txtFirstName.getText().toString().trim();
        String strL = txtLastName.getText().toString().trim();

        if(strF.isEmpty()) {
            tlFirstName.setError("الرجاء ادخال الاسم");
            return false;
        }

        if(strL.isEmpty()) {
            tlLastName.setError("الرجاء ادخال الاسم");
            return false;
        }
        return true;
    }

    private String getDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
        return sdf.format(new Date());
    }

}