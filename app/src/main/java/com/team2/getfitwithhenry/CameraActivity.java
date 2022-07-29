package com.team2.getfitwithhenry;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CameraActivity extends AppCompatActivity {

    //this is for okhttp testing
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final int REQUEST_CODE_PERMISSIONS = 10;
    private String[] REQUIRED_PERMISSIONS = new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private final int CAPTURE_IMAGE_REQUEST = 1;
    private File photoFile;
    private String mCurrentPhotoPath;
    private ImageView mimageView;
    private TextView resultsText;
    ActivityResultLauncher<Intent> captureImageResult;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_camera);


        mimageView = findViewById(R.id.imageView);
        resultsText = findViewById(R.id.resultsText);
        if(havePermission()){
            startCameraAndWriteToFile();
        }
        else{
            requestPermission();
        }

        captureImage();
        registerActivityResult();

    }

    protected Boolean havePermission(){
        if(ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        else{
            return false;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS,REQUEST_CODE_PERMISSIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (havePermission()) {
                startCameraAndWriteToFile();
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void captureImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCameraAndWriteToFile();
        } else {
            String[] permissions = new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
            ActivityCompat.requestPermissions(this, permissions, CAPTURE_IMAGE_REQUEST);
        }
    }


    private void startCameraAndWriteToFile() {
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
                        uploadRequestBody();
                    }
                });
    }

    private void uploadRequestBody() {
        System.out.println("inside method");

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://localhost:8080/test/method1")

                .build();

        new Thread(new Runnable(){
            @Override
            public void run(){
                Log.i("In thread", "run");
                try (Response response = client.newCall(request).execute()) {
                    Log.i("Inside try", "try");
                    if (!response.isSuccessful()) {
                        Log.i("In response", "not success");
                        throw new IOException("Unexpected code " + response);

                    }

                    Log.i("In thread", "received response");
                }
                catch(Exception e){
                    e.printStackTrace();
                    System.out.println("something went wrong");
            }
        }});



    }

    }






