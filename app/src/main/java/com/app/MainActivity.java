package com.app;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends BaseActivity {
    Dialog dialog;
    FirebaseAuth auth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
    }

    public void setFirstLoginByNumberPhone() {
        if (LoginActivity.FIRST_LOGIN_NUMBER_PHONE) {
            setDialog(dialog, this, R.layout.dialog_infor_user);
            final EditText edtName = findViewById(R.id.edt_name);
            final EditText edtBirthday = findViewById(R.id.edt_birthday);
            final RadioButton rabMale = findViewById(R.id.male);
            final RadioButton rabFemale = findViewById(R.id.female);
            Button btnCommit = findViewById(R.id.btn_commit);
            btnCommit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseUser user = auth.getCurrentUser();
                    String userId = user.getUid();

                    reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("id", userId);
                    if (edtName.getText().toString().isEmpty() || edtBirthday.getText().toString().isEmpty()) {

                    } else {
                        hashMap.put("userName", edtName.getText().toString().trim());
                        hashMap.put("userBirthday", edtBirthday.getText().toString().trim());
                        if (rabMale.isChecked()) {
                            hashMap.put("userSex", getResources().getString(R.string.male));
                        }
                        if (rabFemale.isChecked()) {
                            hashMap.put("userSex", getResources().getString(R.string.female));
                        }
                    }
                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                            }
                        }
                    });
                }
            });
        }
    }
}
