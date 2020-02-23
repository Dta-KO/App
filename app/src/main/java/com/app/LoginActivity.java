package com.app;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.tomer.fadingtextview.FadingTextView;

public class LoginActivity extends AppCompatActivity {
    FadingTextView fadingTextView;
    Button btnLoginByPhoneNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

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
                Dialog dialogLogin = new Dialog(LoginActivity.this,R.style.Theme_Dialog);
                dialogLogin.setContentView(R.layout.dialog_login_tel);
                Window window = dialogLogin.getWindow();
                window.setLayout(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
                window.setGravity(Gravity.CENTER);
                dialogLogin.show();

            }
        });

    }
}
