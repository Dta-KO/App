package com.app.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.app.BuildConfig;
import com.app.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tomer.fadingtextview.FadingTextView;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collections;

import static com.facebook.AccessTokenManager.TAG;

public class LoginActivity extends BaseActivity {
    FadingTextView fadingTextView;
    Button btnLoginByPhoneNumber, btnLoginFacebook;
    ImageView heart1, heart2;

    //fire base auth
    FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 101;
    private CallbackManager mCallbackManager;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_login2);
        mAuth = FirebaseAuth.getInstance();


        //init button login facebook
        setBtnLoginFacebook();


        //init fadingTxt
        fadingTextView = findViewById(R.id.fadingTxt);
        fadingTextView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fadeout));

        //init button login phone
        setBtnLoginByPhoneNumber();


        //init heart image
        heart1 = findViewById(R.id.heart1);
        heart2 = findViewById(R.id.heart2);
    }

    private void setBtnLoginFacebook() {

        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        Log.d("Success", "Login");
                        if (AccessToken.getCurrentAccessToken() != null) {
                            GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response) {
                                    if (object != null) {
                                        try {
                                            AppEventsLogger logger = AppEventsLogger.newLogger(LoginActivity.this);
                                            logger.logEvent("Facebook login success");

                                            handleFacebookAccessToken(loginResult.getAccessToken());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }
                            });
                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "id,name,email,gender, birthday, about");
                            request.setParameters(parameters);
                            request.executeAsync();
                        }
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(LoginActivity.this, "Login Cancel", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(LoginActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        btnLoginFacebook = findViewById(R.id.btn_dang_nhap_facebook);
        btnLoginFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "email"));
            }
        });

    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        mAuth = FirebaseAuth.getInstance();

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                Log.i(TAG, "email" + user.getEmail());
                                changeToMainActivity();
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }


                    }
                });
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
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();
        if (user != null && reference.child("User").child(user.getUid()).equals(user.getUid())) {
            changeToMainActivity();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
                if (mUser != null) {
                    Log.d("User", mUser.getPhoneNumber());

                    changeToMainActivity();
                } else {
                    if (response != null) {
                        Log.d("Error", String.valueOf(response.getError()));
                    }
                }
            }
        }
    }


    public void changeToMainActivity() {
        setImageHeart();
    }

    //init image heart
    public void setImageHeart() {
        heart1.setVisibility(View.VISIBLE);
        heart2.setVisibility(View.VISIBLE);
        heart2.setTranslationX(BaseActivity.width - heart2.getLayoutParams().width);
        heart1.animate().translationX(BaseActivity.width / 2 - (heart1.getLayoutParams().width >> 1)).withLayer();
        heart1.animate().setDuration(1500);
        heart2.animate().translationX(BaseActivity.width / 2).withLayer().withEndAction(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        heart2.animate().setDuration(1500);
    }

}
