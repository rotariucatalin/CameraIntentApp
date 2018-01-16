package com.example.user.cameraintentapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    public static final int OPEN_CAMERA_CODE = 0;
    private ImageView imageView;
    private String imageFileLocation = "";
    private boolean permissionGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imagePreview);
        /*
        *   Every tme the app is re
        * */
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE }, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == OPEN_CAMERA_CODE) {
            //Toast.makeText(this, "Photo takend successfully", Toast.LENGTH_SHORT).show();
            //Bundle extras = data.getExtras();
            //Bitmap imageBitMap = (Bitmap) extras.get("data");
            //imageView.setImageBitmap(imageBitMap);

            /*
            *   Convert image from path into bitmap and set it to imageview
            * */
            Bitmap photoCapturedBitmap = BitmapFactory.decodeFile(imageFileLocation);

            /*
            *   Rotate the image based on phone orientation
            * */
            ExifInterface ei = null;
            try {
                ei = new ExifInterface(imageFileLocation);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

            Bitmap rotatedBitmap = null;
            switch(orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(photoCapturedBitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(photoCapturedBitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(photoCapturedBitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = photoCapturedBitmap;
            }

            imageView.setImageBitmap(rotatedBitmap);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    permissionGranted = true;
                else
                    permissionGranted = false;
            }
        }

    }

    /*
        *   Send intent to camera app to start
        * */
    public void takePhoto(View view) {

        if(permissionGranted) {

            Intent openCameraIntent = new Intent();
            openCameraIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

            /*
            *   Retrieve the photo file
            * */

            File photoFile = null;

            try {
                photoFile = createImageFile();

            } catch (IOException e) {
                e.printStackTrace();
            }

            openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            startActivityForResult(openCameraIntent, OPEN_CAMERA_CODE);
        } else {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE }, 1);

        }
    }

    public File createImageFile() throws IOException {

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFilename    = "IMAGE_" + timestamp + "_";
        File storageDirectory   = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File image  = File.createTempFile(imageFilename, ".jpg", storageDirectory);
        imageFileLocation   = image.getAbsolutePath();

        return image;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }
}
