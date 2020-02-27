package com.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tomer.fadingtextview.FadingTextView;

import java.util.Collections;

public class LoginActivity extends BaseActivity {
    FadingTextView fadingTextView;
    Button btnLoginByPhoneNumber;
    ImageView heart1, heart2;

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

        //init heart image
        initImageHeart();
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
                if (response != null) {
                    Log.d("Error", String.valueOf(response.getError()));
                }
            }
        }
    }

    //init image heart
    public void initImageHeart() {
        heart1 = findViewById(R.id.heart1);
        heart2 = findViewById(R.id.heart2);
        heart2.setTranslationX(BaseActivity.width);
    }
}
