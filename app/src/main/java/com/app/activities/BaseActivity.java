package com.app.activities;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.app.R;
import com.app.model.InforImage;
import com.app.model.User;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sdsmdg.tastytoast.TastyToast;

public class BaseActivity extends AppCompatActivity {
    public static float width;
    public float height;
    public static final int AVATAR = 1;
    public static final int BACKGROUND_HEADER = 2;
    public static final int HEADER_AVATAR = 3;
    public User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
    }

    public void setDialog(Dialog dialog, Context context, int layout) {
        dialog.setContentView(layout);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
    }

    public void chooseFile(int requestCode) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, requestCode);
    }

    public String getExtension(Uri uri) {
        ContentResolver resolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(resolver.getType(uri));
    }

    public void uploadFile(final Uri imgUri, final StorageReference mStorageRef, final DatabaseReference mDatabaseRef) {
        final float idImage = System.currentTimeMillis();
        final StorageReference reference = mStorageRef.child(idImage + "." + getExtension(imgUri));
        reference.putFile(imgUri)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        TastyToast.makeText(getApplicationContext(), getResources().getString(R.string.error), TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        String name = taskSnapshot.getMetadata().getName();
                        writeNewImageInfoToDB(0, name, mDatabaseRef);
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        // progress percentage
                    }
                })
                .addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onPaused(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        TastyToast.makeText(getApplicationContext(), getResources().getString(R.string.error), TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                    }
                });
    }

    private void writeNewImageInfoToDB(int likeCounts, String id, DatabaseReference reference) {
        InforImage info = new InforImage(likeCounts, id);

        String key = reference.push().getKey();
        reference.child(key).setValue(info).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                TastyToast.makeText(getApplicationContext(), getResources().getString(R.string.ok), TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
            }
        });
    }


    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public void getIdLastImage(DatabaseReference mDataReference, final StorageReference mStorageRef, final ImageView view) {
        final Query query = mDataReference.orderByChild("idImage").limitToLast(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> key = dataSnapshot.getChildren();
                for (DataSnapshot item : key) {
                    InforImage inforImage = item.getValue(InforImage.class);
                    getImageFromUrl(inforImage.idImage, mStorageRef, view);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getImageFromUrl(String idImage, StorageReference mStorageRef, final ImageView view) {
        StorageReference storageImage = mStorageRef.child(idImage);
        storageImage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext()).load(uri).into(view);
                user.setAvt(String.valueOf(uri));
            }
        });
    }
}
