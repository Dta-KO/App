package com.app.activities;

import android.app.Activity;
import android.app.AppComponentFactory;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.app.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sdsmdg.tastytoast.TastyToast;

public class BaseActivity extends AppCompatActivity {
    public static float width;
    public float height;
    public static Uri downloadUrl;
    public float timeNow;

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

    public void chooseFile() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    public String getExtension(Uri uri) {
        ContentResolver resolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(resolver.getType(uri));
    }

    public void uploadFile(Uri imgUri, StorageReference mStorageRef) {
        timeNow = System.currentTimeMillis();

        StorageReference reference = mStorageRef.child(timeNow + "." + getExtension(imgUri));
        reference.putFile(imgUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        downloadUrl = taskSnapshot.getUploadSessionUri();
                        Log.d("url image: ", String.valueOf(downloadUrl));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception exception) {
                        TastyToast.makeText(getApplicationContext(), getResources().getString(R.string.error), TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                    }
                });
    }

    public ImageView imageView;

    public Drawable getImageFromUrl(StorageReference mStorage) {
        StorageReference dateRef = mStorage.child("/" + "1584784329271.png");
        imageView = new ImageView(getApplicationContext());
        dateRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri downloadUrl) {
                //do something with downloadurl
                Glide.with(getApplicationContext()).load(downloadUrl).into(imageView);
                Log.d("a", String.valueOf(imageView.getDrawable()));
            }
        });

        return imageView.getDrawable();
    }

}
