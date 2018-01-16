package com.example.user.cameraintentapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final int OPEN_CAMERA_CODE = 0;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imagePreview);
    }

    public void takePhoto(View view) {

        Intent openCameraIntent = new Intent();
        openCameraIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(openCameraIntent, OPEN_CAMERA_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == OPEN_CAMERA_CODE) {
            //Toast.makeText(this, "Photo takend successfully", Toast.LENGTH_SHORT).show();
            Bundle extras = data.getExtras();
            Bitmap imageBitMap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitMap);
        }
    }
}
