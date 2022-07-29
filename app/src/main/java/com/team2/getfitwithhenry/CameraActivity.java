package com.team2.getfitwithhenry;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class CameraActivity extends AppCompatActivity {

    private final OkHttpClient client = new OkHttpClient();
    private static final MediaType MEDIA_TYPE_PLAINTEXT = MediaType
            .parse("text/plain; charset=utf-8");
    private final int REQUEST_CODE_PERMISSIONS = 10;
    private final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private File photoFile;
    private ImageView mimageView;
    private TextView resultsText;
    private Bitmap bitImage;
    ActivityResultLauncher<Intent> captureImageResult;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_camera);

        mimageView = findViewById(R.id.imageView);
        resultsText = findViewById(R.id.resultsText);

        registerActivityResult();

        if (havePermission()) {
            startCameraAndWriteToFile();
        } else {
            requestPermission();
        }


    }

    protected Boolean havePermission() {
        return ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
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
            int CAPTURE_IMAGE_REQUEST = 1;
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
                ex.printStackTrace();
            }
        } else {
            Toast.makeText(getBaseContext(), "Nothing to Show", Toast.LENGTH_LONG).show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = DateFormat();
        String imgFileName = "JPEG_" + timeStamp + "_";
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //File myFile = new File(dir, imgFileName);
        //String mCurrentPhotoPath = myFile.getAbsolutePath();
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
                        bitImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                        mimageView.setImageBitmap(bitImage);
                        Toast.makeText(getBaseContext(), "Showing the image", Toast.LENGTH_LONG).show();
                        uploadRequestBody();
                    }
                });
    }

    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
    }

    private void uploadRequestBody() {

        Request request = new Request.Builder()
                .url("http://192.168.0.111:8080/flask/recieveImgFromAndroid")
                .post(RequestBody.create(MEDIA_TYPE_PLAINTEXT, getBytesFromBitmap(bitImage)))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    ResponseBody responseBody = response.body();
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code " + response);
                    }

                    String msg = String.valueOf(responseBody.string());

                    backgroundThreadShortToast(getApplicationContext(), msg);

                    Log.i("data", responseBody.string());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public static void backgroundThreadShortToast(final Context context, final String msg) {
        if (context != null && msg != null) {
            new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show());
        }
    }

}






