package com.armjld.busaty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.armjld.busaty.ActionClasses.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;

import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;

public class ReciveOTPScreen extends AppCompatActivity {

    public static String phone = "";
    String mVerificationId = "";
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    ProgressDialog mDialog;
    TextView txtTitle;
    OtpView txtCode;
    FirebaseAuth mAuth;

    DatabaseReference uDatabase = FirebaseDatabase.getInstance().getReference().child("users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recive_otpscreen);

        mAuth = FirebaseAuth.getInstance();
        txtTitle = findViewById(R.id.txtTitle);
        txtCode = findViewById(R.id.txtCode);
        mDialog = new ProgressDialog(this);


        txtCode.setOtpCompletionListener(otp -> verifyPhoneNumberWithCode(mVerificationId, otp));

        txtTitle.setOnClickListener(v-> {
            verifyPhoneNumberWithCode(mVerificationId, txtCode.getMaskingChar());
        });

        UpdateUI();
    }

    private void UpdateUI() {
        txtTitle.setText("الرجاء ادخال الكود الذي سيتم ارساله الي رقم هاتفك : " + phone);
        sendOTP();
    }

    private void sendOTP() {
        mDialog.setMessage("جاري ارسال الكود ..");
        mDialog.setCancelable(false);
        mDialog.show();

        if(mCallbacks == null) {
            mCallBack();
        }

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+2" + phone)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        mDialog.setMessage("جاري التأكد من الرمز ..");
        mDialog.setCancelable(false);
        mDialog.show();

        if(verificationId.equals("")) {
            mDialog.dismiss();
            return;
        }

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        checkForUser(credential);
    }

    private void checkForUser(PhoneAuthCredential credential) {
        uDatabase.orderByChild("phone").equalTo(phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    login(credential);
                } else {
                    signUp(credential);
                }

                mDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void signUp(PhoneAuthCredential credential) {
        SignUp.authCredential = credential;
        SignUp.phone = phone;
        startActivity(new Intent(this, SignUp.class));
        finish();
    }


    private void login(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                LoginManager loginManager = new LoginManager(this);
                loginManager.setMyInfo();
            } else {
                Toasty.error(ReciveOTPScreen.this, "حدث خطأ ما، الرجاء المحاوله مره اخري", Toasty.LENGTH_LONG, true).show();
                finish();
            }
        });
    }

    // --------------------------------- Phone Number Functions -------------------------- //
    private void mCallBack() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                txtCode.setText(credential.getSmsCode());
                mDialog.dismiss();
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                mDialog.dismiss();
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toasty.error(ReciveOTPScreen.this, "رقم الهاتف غير صحيح", Toast.LENGTH_SHORT, true).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Snackbar.make(findViewById(android.R.id.content), "تم ارسال العديد من الرسائل، الرجاء المحاوله لاحقا", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(verificationId, token);
                Toasty.success(ReciveOTPScreen.this, "تم ارسال الرساله", Toast.LENGTH_SHORT, true).show();
                mDialog.dismiss();
                Toast.makeText(ReciveOTPScreen.this, " Verf code = " + verificationId, Toast.LENGTH_SHORT).show();
                mVerificationId = verificationId;
            }
        };
    }
}