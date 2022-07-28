package com.team2.getfitwithhenry;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraActivity extends AppCompatActivity {

    private final int CAPTURE_IMAGE_REQUEST = 1;
    private File photoFile;
    private String mCurrentPhotoPath;
    private Button mbtncapture;
    private ImageView mimageView;
    ActivityResultLauncher<Intent> captureImageResult;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_camera);

        mbtncapture = findViewById(R.id.btncapture);
        mimageView = findViewById(R.id.imageView);
        mbtncapture.setOnClickListener(view -> captureImage());

        registerActivityResult();

    }


    private void captureImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCameraandWritetoFile();
        } else {
            String[] permissions = new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
            ActivityCompat.requestPermissions(this, permissions, CAPTURE_IMAGE_REQUEST);
        }
    }

    private void startCameraandWritetoFile() {
        Intent captureImageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (captureImageIntent.resolveActivity(getPackageManager()) != null) {
            try {
                photoFile = createImageFile();
                Uri photoURI = FileProvider.getUriForFile(
                        this,
                        "com.team2.getfitwithhenry.fileprovider",
                        photoFile
                );
                captureImageIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                captureImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                //startActivityForResult(captureImageIntent, CAPTURE_IMAGE_REQUEST);
                captureImageResult.launch(captureImageIntent);
            } catch (Exception ex) {
                Toast.makeText(getBaseContext(), ex.getMessage().toString(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getBaseContext(), "Noting to Show", Toast.LENGTH_LONG).show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = DateFormat();
        String imgFileName = "JPEG_" + timeStamp + "_";
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File myFile = new File(dir, imgFileName);
        mCurrentPhotoPath = myFile.getAbsolutePath();
        return (File.createTempFile(imgFileName, ".jpg", dir));
    }

    private String DateFormat() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
    }

    protected void registerActivityResult() {
        captureImageResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                        Intent data = result.getData();

                        Bitmap bitImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                        mimageView.setImageBitmap(bitImage);
                        Toast.makeText(getBaseContext(), "Showing the image", Toast.LENGTH_LONG).show();
                    }
                });
    }
}
