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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.team2.getfitwithhenry.model.Constants;
import com.team2.getfitwithhenry.model.Ingredient;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class CameraActivity extends AppCompatActivity implements View.OnClickListener, WrongIngredientFragment.IWrongIngredientFragment {

    private final OkHttpClient client = new OkHttpClient();
    private static final MediaType MEDIA_TYPE_PLAINTEXT = MediaType.parse("text/plain; charset=utf-8");
    private final int REQUEST_CODE_PERMISSIONS = 10;
    private File photoFile;
    private String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    ActivityResultLauncher<Intent> captureImageResult;

    private ImageView mImageView;
    private TextView resultsText;
    private Button goToSearchBtn;
    private Button incorrectBtn;
    private Button retakeBtn;

    private Ingredient iPredict;
    //private String returnMsg;

    //consider not making this global?
    private String mCurrentPhotoPath;
    private Bitmap bitImage;
    private boolean hasPermission = false;


    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_camera);

        mImageView = findViewById(R.id.imageView);
        resultsText = findViewById(R.id.resultsText);

        goToSearchBtn = findViewById(R.id.goToSearchBtn);
        incorrectBtn = findViewById(R.id.incorrectBtn);
        retakeBtn = findViewById(R.id.retake_photo);

        goToSearchBtn.setOnClickListener(this);
        incorrectBtn.setOnClickListener(this);
        retakeBtn.setOnClickListener(this);

        registerActivityResult();
        checkPermissions();
        deletePicturesFromPath();
        if (hasPermission)
            startCameraAndWriteToFile();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        if (v.getId() == R.id.goToSearchBtn) {
            List<Ingredient> iList = Arrays.asList(iPredict);
            intent = new Intent(this, AddMealActivity.class);
            intent.putExtra("ingredients", (Serializable) iList);
            startActivity(intent);
        } else if (v.getId() == R.id.incorrectBtn) {
            WrongIngredientFragment wf = new WrongIngredientFragment();
            Bundle args = new Bundle();
            args.putString("predicted", iPredict.getName());
            wf.setArguments(args);
            wf.show(getSupportFragmentManager(), "Wrong Ingredient Fragment");
        } else if (v.getId() == R.id.retake_photo) {
            startCameraAndWriteToFile();
        }
    }

    @Override
    public void itemClicked(String content){
//        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
        oopsModelGotItWrong(content);
    }


    public void checkPermissions() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(getBaseContext(), permission) == PackageManager.PERMISSION_GRANTED) {
                hasPermission = true;
            } else {
                hasPermission = false;
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            for (String permission : REQUIRED_PERMISSIONS) {
                if (ContextCompat.checkSelfPermission(getBaseContext(), permission) == PackageManager.PERMISSION_GRANTED) {
                    hasPermission = true;
                }
            }
        } else {
            hasPermission = false;
            Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
            finish();
        }

        if (hasPermission)
            startCameraAndWriteToFile();

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
        String imgFileName = "DIETPHOTO_" + timeStamp + "_";
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
                        mImageView.setImageBitmap(bitImage);
//                        Toast.makeText(getBaseContext(), "Showing the image", Toast.LENGTH_LONG).show();
                        uploadRequestBody();
                    } else if (result.getResultCode() == RESULT_CANCELED) {
                        finish();
                    }
                    else {
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
                .url(Constants.javaURL +"/flask/recieveImgFromAndroid")
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

                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.registerModule(new JavaTimeModule());
                    iPredict = objectMapper.readValue(responseBody.string(), Ingredient.class);
                    displayResponse(getApplicationContext(), iPredict);

                    response.body().close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void oopsModelGotItWrong(String actual) {
        JSONObject postData = new JSONObject();
        try {
            postData.put("actual", actual);
            postData.put("predicted", iPredict.getName());

            StringBuilder sb = new StringBuilder();
            sb.append("data:image/png;base64,");
            sb.append(StringUtils.newStringUtf8(Base64.encodeBase64(getBytesFromBitmap(bitImage), false)));
            postData.put("photoString", sb.toString());

        }
        catch (JSONException ex){
            ex.printStackTrace();
        }
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(postData.toString(), JSON);

        Request request = new Request.Builder()
                .url(Constants.javaURL +"/flask/oopsModelGotItWrong")
                .post(body)
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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(CameraActivity.this, "Oops! Something went wrong...", Toast.LENGTH_SHORT).show();
                            }
                        });
                        throw new IOException("Unexpected code " + response);
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CameraActivity.this, "We received the photo! Thank you for submitting!", Toast.LENGTH_SHORT).show();
                        }
                    });

                    Log.i("data", responseBody.string());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void displayResponse(final Context context, final Ingredient iPredict) {
        if (context != null && iPredict != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {

                @Override
                public void run() {
                    resultsText.setText(iPredict.getName() + " (100g)" + "\n" + iPredict.getNutritionRecord().getTruncNutrition());
                    goToSearchBtn.setVisibility(View.VISIBLE);
                    incorrectBtn.setVisibility(View.VISIBLE);
                    retakeBtn.setVisibility(View.VISIBLE);

                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        deletePicturesFromPath();
    }

    private void deletePicturesFromPath() {
        File[] dietPhotos = getExternalFilesDir(Environment.DIRECTORY_PICTURES).listFiles();
        for (File photo : dietPhotos) {
            photo.delete();
        }
    }
}






