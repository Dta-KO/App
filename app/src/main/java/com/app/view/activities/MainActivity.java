package com.app.view.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.app.R;
import com.app.model.InforImage;
import com.app.model.User;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sdsmdg.tastytoast.TastyToast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity {
    private Dialog dialog;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private DatabaseReference mDatabaseRef;
    private EditText edtName, edtBirthday;
    private RadioButton rabMale, rabFemale;
    private CircleImageView avatar, headerUserAvatar;
    private Button btnCommit;
    private User user;
    private StorageReference mStorageRef;
    private Uri imgUri;
    private AppBarConfiguration mAppBarConfiguration;
    private ImageView headerUserBackground;
    private DrawerLayout drawer;
    private NavigationView navigationView, navigationFooter;
    private TextView headerUserName, headerUserDescription;
    private ArrayList<InforImage> listInforImages;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        mStorageRef = FirebaseStorage.getInstance().getReference("Images").child(firebaseUser.getUid());
        //set first login
        //get profile and set drawer layout
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("User").child(firebaseUser.getUid());
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    setFirstLogin();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    setDrawerNavigation(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void init() {
        //main activity
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        //navigation main
        navigationView = findViewById(R.id.nav_view);
        //navigation footer
        navigationFooter = findViewById(R.id.nav_footer);

        //for header navigation
        View childView = navigationView.getHeaderView(0);
        headerUserAvatar = childView.findViewById(R.id.header_img);
        headerUserName = childView.findViewById(R.id.header_user_name);
        headerUserDescription = childView.findViewById(R.id.header_user_description);
        headerUserBackground = childView.findViewById(R.id.header_background);

        //for list image
        listInforImages = new ArrayList<>();
    }

    //set dialog for get information user in first login
    public void setFirstLogin() {
        //dialog first login
        dialog = new Dialog(this);
        setDialog(dialog, this, R.layout.dialog_infor_user);
        edtName = dialog.findViewById(R.id.edt_name);
        edtBirthday = dialog.findViewById(R.id.edt_birthday);
        rabMale = dialog.findViewById(R.id.male);
        rabFemale = dialog.findViewById(R.id.female);
        avatar = dialog.findViewById(R.id.avatar);
        btnCommit = dialog.findViewById(R.id.btn_commit);

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFile(BaseActivity.AVATAR);
            }
        });
        edtBirthday.setOnClickListener(new View.OnClickListener() {
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DATE);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(MainActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(year, month, dayOfMonth);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        edtBirthday.setText(simpleDateFormat.format(calendar.getTime()));
                    }
                }, year, month, day);
                dialog.show();

            }
        });

        btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtName.getText().toString().trim();
                String birthday = edtBirthday.getText().toString();
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(birthday)) {
                    TastyToast.makeText(getApplicationContext(), getResources().getString(R.string.dien_thieu_infor), TastyToast.LENGTH_SHORT, TastyToast.WARNING);
                } else {
                    user = new User();
                    user.setName(name);
                    user.setBirthday(birthday);
                    if (rabFemale.isChecked()) {
                        user.setSex(getResources().getString(R.string.female));
                    } else {
                        user.setSex(getResources().getString(R.string.male));
                    }
                    user.getId();
                    mDatabaseRef.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            TastyToast.makeText(getApplicationContext(), getResources().getString(R.string.nhap_infor_success),
                                    TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
                            dialog.dismiss();
                        }
                    });
                }
            }
        });
    }


    //setting toàn bộ drawer layout
    public void setDrawerNavigation(User user) {
        navigationFooter.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_logout) {
                    signOut();
                }
                return false;
            }
        });
        setInformationUserToHeadLayout(user);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        navigationView.setPadding(0, getStatusBarHeight(), 0, 0);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_chat, R.id.nav_mini_game, R.id.nav_about, R.id.nav_setting, R.id.nav_logout)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_main_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

    }

    public void setInformationUserToHeadLayout(User user) {

        headerUserName.setText(user.getName());
        headerUserDescription.setText(user.getBirthday());
        headerUserAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFile(BaseActivity.HEADER_AVATAR);
            }
        });

        headerUserBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFile(BaseActivity.BACKGROUND_HEADER);
            }
        });

        //set avatar
        DatabaseReference avatarRef = mDatabaseRef.child("avatar");
        StorageReference avatarSto = mStorageRef.child("avatar");
        getIdLastImage(avatarRef, avatarSto, headerUserAvatar);

        //set background header
        DatabaseReference backgroundRef = mDatabaseRef.child("backgroundHeader");
        StorageReference backgroundSto = mStorageRef.child("background_header");
        getIdLastImage(backgroundRef, backgroundSto, headerUserBackground);
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_main_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            imgUri = data.getData();
            switch (requestCode) {
                case BaseActivity.AVATAR:
                    avatar.setImageURI(imgUri);
                    headerUserAvatar.setImageURI(imgUri);
                    StorageReference imageProfile = mStorageRef.child("avatar");
                    DatabaseReference avatarRef = mDatabaseRef.child("avatar");
                    uploadFile(imgUri, imageProfile, avatarRef);
                    break;
                case BaseActivity.HEADER_AVATAR:
                    headerUserAvatar.setImageURI(imgUri);
                    StorageReference imageProfileHeader = mStorageRef.child("avatar");
                    DatabaseReference headerRef = mDatabaseRef.child("avatar");
                    uploadFile(imgUri, imageProfileHeader, headerRef);
                    break;
                case BaseActivity.BACKGROUND_HEADER:
                    headerUserBackground.setImageURI(imgUri);
                    StorageReference imageBackgroundHeader = mStorageRef.child("background_header");
                    DatabaseReference backgroundHeaderRef = mDatabaseRef.child("backgroundHeader");
                    uploadFile(imgUri, imageBackgroundHeader, backgroundHeaderRef);
                    break;

            }
        }
    }

    public void signOut() {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

}
