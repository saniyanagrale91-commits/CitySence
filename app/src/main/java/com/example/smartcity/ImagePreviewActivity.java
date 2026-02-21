package com.example.smartcity;


import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class ImagePreviewActivity extends AppCompatActivity {

    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);

        img = findViewById(R.id.imgFull);

        String path = getIntent().getStringExtra("imagePath");
        if (path != null) {
            img.setImageBitmap(BitmapFactory.decodeFile(path));
        }
    }
}