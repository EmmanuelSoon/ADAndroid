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
import android.view.View;
import android.widget.Button;
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

public class CameraActivity extends AppCompatActivity implements View.OnClickListener {

    private final OkHttpClient client = new OkHttpClient();
    private static final MediaType MEDIA_TYPE_PLAINTEXT = MediaType.parse("text/plain; charset=utf-8");
    private final int REQUEST_CODE_PERMISSIONS = 10;
    private File photoFile;
    private String[] REQUIRED_PERMISSIONS = new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    ActivityResultLauncher<Intent> captureImageResult;

    private ImageView mimageView;
    private TextView resultsText;
    private Button goToSearchBtn;
    private Button incorrectBtn;
    private String returnMsg;


    //consider not making this global?
    private String mCurrentPhotoPath;
    private Bitmap bitImage;



    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_camera);

        mimageView = findViewById(R.id.imageView);
        resultsText = findViewById(R.id.resultsText);

        goToSearchBtn = findViewById(R.id.goToSearchBtn);
        incorrectBtn = findViewById(R.id.incorrectBtn);

        goToSearchBtn.setOnClickListener(this);
        incorrectBtn.setOnClickListener(this);

        registerActivityResult();
        checkPermissions();

    }

    @Override
    public void onClick(View v) {
        Intent intent;
        if(v.getId() == R.id.goToSearchBtn){
            intent = new Intent(this, SearchFoodActivity.class);
            intent.putExtra("SearchValue", returnMsg);
            startActivity(intent);
        }
        else if(v.getId() == R.id.incorrectBtn){
            Toast.makeText(this, "Oh no! Classifier got it wrong.", Toast.LENGTH_SHORT).show();
        }
    }

    public void checkPermissions(){
        for (String permission : REQUIRED_PERMISSIONS){
            if(ContextCompat.checkSelfPermission(getBaseContext(), permission) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS,REQUEST_CODE_PERMISSIONS);
            }

        }
        startCameraAndWriteToFile();
    }

    @Override
    //work in progress, to degrade app gracefully
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            for (String permission : REQUIRED_PERMISSIONS) {
                if (ContextCompat.checkSelfPermission(getBaseContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                    finish();   //finishing here will result in the camera activity closing
                }
            }
            startCameraAndWriteToFile();
        }

    }




    private void startCameraAndWriteToFile() {
        Intent captureImageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (captureImageIntent.resolveActivity(getPackageManager()) != null) {
            try {
                //note to alyssa -> dont set this as global, try to get the file from the intent
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
                        bitImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                        mimageView.setImageBitmap(bitImage);
                        Toast.makeText(getBaseContext(), "Showing the image", Toast.LENGTH_LONG).show();
                        uploadRequestBody();
                    }
                    else{
                        System.out.println(result.getResultCode());
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

                    returnMsg = String.valueOf(responseBody.string());
                    displayResponse(getApplicationContext(), returnMsg);

                    Log.i("data", responseBody.string());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }

    public void displayResponse(final Context context, final String msg) {
        if (context != null && msg != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {

                @Override
                public void run() {
                    resultsText.setText(msg);
                    goToSearchBtn.setVisibility(View.VISIBLE);
                    incorrectBtn.setVisibility(View.VISIBLE);
                }
            });
        }
    }

}






