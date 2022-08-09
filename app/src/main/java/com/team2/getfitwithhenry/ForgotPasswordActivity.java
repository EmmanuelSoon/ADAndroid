package com.team2.getfitwithhenry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.team2.getfitwithhenry.model.Constants;
import com.team2.getfitwithhenry.model.User;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView mBackBtn;
    private Button mNextBtn;
    private EditText mEmail;
    private TextView mValidation;
    private User user;
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        init();

        mBackBtn.setOnClickListener(this);
        mNextBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        int id = v.getId();
        if(id == R.id.nextBtn){
            try{
                searchUser(mEmail.getText().toString());
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        else if(id == R.id.backBtn){
            startLoginActivity();
        }
    }

    private void init(){
        mBackBtn = findViewById(R.id.backBtn);
        mNextBtn = findViewById(R.id.nextBtn);
        mEmail = findViewById(R.id.email);
        mValidation = findViewById(R.id.validationForgotPassword);
    }
    private boolean isValidEmail(String email){
        String mailFormat =  "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(mailFormat);
        Matcher matcher = pattern.matcher(email);
        if(matcher.matches())
            return true;
        return false;
    }

    private void searchUser(String email) throws JSONException{
        if(email.isEmpty()){
            showErrorMsg("Field cannot be empty");
            return;
        }
        if (isValidEmail(email)){
            user = new User(email);

            JSONObject userObj = new JSONObject();
            userObj.put("email", user.getUsername());

            validateUsernameFromDetails(userObj);
        }
        else{
            showErrorMsg("Invalid Email Address");
        }
    }
    private void showErrorMsg(String msg){
        if(msg != null){
            mValidation.setText(msg);
            mValidation.setVisibility(View.VISIBLE);
        }
    }

    private void validateUsernameFromDetails(JSONObject userObj){
        MediaType JsonObj = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(JsonObj, userObj.toString());

        Request request = new Request.Builder().url(Constants.javaURL +"/forgotPassword/validateUsername").post(requestBody).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                ResponseBody responseBody = response.body();

                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());


                if (responseBody.contentLength() != 0)
                    user = objectMapper.readValue(responseBody.string(), User.class);
                else
                    user = null;

                if(user == null){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showErrorMsg("Email Doesn't Exist In Our System");
                        }
                    });
                }
                else{
                    sendEmail(user.getUsername());
                    startLoginActivity();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"Check Your Email To Retrieve Your Password!",Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }
    private void sendEmail(String email){

        try{
            String senderEmail = "team2henry@gmail.com";
            String senderPassword = "ocpoyqxenzziyrzt";
            String receiverEmail = email;

            String stringHost = "smtp.gmail.com";

            Properties properties = System.getProperties();
            properties.put("mail.smtp.host", stringHost);
            properties.put("mail.smtp.port","465");
            properties.put("mail.smtp.ssl.enable","true");
            properties.put("mail.smtp.auth", "true");

            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(senderEmail, senderPassword);
                }
            });

            MimeMessage mimeMessage = new MimeMessage(session);

            mimeMessage.addRecipient(Message.RecipientType.TO, (new InternetAddress(receiverEmail)));
            mimeMessage.setSubject("Password Recovery Mail");
            mimeMessage.setText("Dear " + user.getName()+",\n\nYour password is "+user.getPassword()+".\n\nBest Regards!");

            Thread bkdgThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        Transport.send(mimeMessage);
                    }catch (MessagingException e){
                        e.printStackTrace();
                    }
                }
            });
            bkdgThread.start();

        }catch (AddressException e){
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
    private void startLoginActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}