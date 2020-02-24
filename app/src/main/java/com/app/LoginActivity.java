package com.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tomer.fadingtextview.FadingTextView;

import java.util.Collections;

public class LoginActivity extends AppCompatActivity {
    FadingTextView fadingTextView;
    Button btnLoginByPhoneNumber;
//    //Dialog
//    Dialog dialogLogin;
//    Button btnClear, btnLoginNow;
//    EditText edtPhone;
//    TextView txtSuCo;

    //fire base auth
    FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        mAuth = FirebaseAuth.getInstance();

        //init fadingTxt
        fadingTextView = findViewById(R.id.fadingTxt);
        fadingTextView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fadeout));

        //init button login phone
        setBtnLoginByPhoneNumber();
    }

    //init button login phone
    public void setBtnLoginByPhoneNumber() {
        btnLoginByPhoneNumber = findViewById(R.id.btn_dang_nhap_sdt);
        btnLoginByPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = AuthUI.getInstance().createSignInIntentBuilder()
                        .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                        .setAvailableProviders(Collections.singletonList(new AuthUI.IdpConfig.PhoneBuilder().build()))
                        .setLogo(R.mipmap.ic_launcher)
                        .build();
                startActivityForResult(intent, RC_SIGN_IN);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
                if (mUser != null) {
                    Log.d("User", mUser.getPhoneNumber());
                }
            } else {
                Log.d("Error", String.valueOf(response.getError()));
            }
        }
    }
}
