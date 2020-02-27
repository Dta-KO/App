package com.app;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;

import androidx.annotation.Nullable;

public class BaseActivity extends Activity {
    public static int width;
    public int height;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
    }
}
