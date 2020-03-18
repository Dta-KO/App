package com.app.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.annotation.NonNull;

import com.app.R;
import com.app.model.User;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.sdsmdg.tastytoast.TastyToast;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity {
    private Dialog dialog;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private EditText edtName, edtBirthday;
    private RadioButton rabMale, rabFemale;
    private CircleImageView avatar;
    private Button btnCommit;
    private User user;
    private StorageReference mStorageRef;
    private Uri imgUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        //set first login
        //get profile and set drawer layout
        reference = FirebaseDatabase.getInstance().getReference().child("User").child(firebaseUser.getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
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
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                setDrawerNavigation();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    //set dialog for get information user in first login
    public void setFirstLogin() {
        dialog = new Dialog(this);
        setDialog(dialog, this, R.layout.dialog_infor_user);
        edtName = dialog.findViewById(R.id.edt_name);
        edtBirthday = dialog.findViewById(R.id.edt_birthday);
        rabMale = dialog.findViewById(R.id.male);
        rabFemale = dialog.findViewById(R.id.female);
        avatar = dialog.findViewById(R.id.avatar);

        btnCommit = dialog.findViewById(R.id.btn_commit);
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
                    avatar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            chooseFile();
                        }
                    });

                    reference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
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
    public void setDrawerNavigation() {
        new DrawerBuilder().withActivity(this).build();
        //if you want to update the items at a later time it is recommended to keep it in a variable
        PrimaryDrawerItem item = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.drawer_item_home);
        SecondaryDrawerItem itemSetting = new SecondaryDrawerItem().withIdentifier(2).withName(R.string.drawer_item_settings).withIcon(R.drawable.ic_settings);
        final SecondaryDrawerItem itemLogout = new SecondaryDrawerItem().withIdentifier(3).withName(R.string.logout).withIcon(R.drawable.ic_exit);
        SecondaryDrawerItem itemAbout = new SecondaryDrawerItem().withIdentifier(4).withName(R.string.about).withIcon(R.drawable.ic_info);

        //default user
//        AccountHeader headerResult = new AccountHeaderBuilder()
//                .withActivity(this)
//                .withHeaderBackground(Drawable.createFromPath(user.getAvt()))
//                .addProfiles(
//                        new ProfileDrawerItem().withName(user.getName()).withEmail(user.getId())
//
//                )
//                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
//                    @Override
//                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
//                        return false;
//                    }
//                })
//                .build();

        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar((androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar1))
                .addDrawerItems(
                        item,
                        new DividerDrawerItem(),
                        itemAbout,
                        itemSetting
                )
//                .withAccountHeader(headerResult)
                .build();
        result.addStickyFooterItem(itemLogout);
        itemLogout.withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                signOut();
                return true;
            }
        });

    }

    public void chooseFile() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    public void uploadFile() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imgUri = data.getData();
            avatar.setImageURI(imgUri);
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
