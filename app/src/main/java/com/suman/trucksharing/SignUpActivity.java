package com.suman.trucksharing;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.suman.trucksharing.DB.UserDBHandler;
import com.suman.trucksharing.DB.UserModel;
import com.suman.trucksharing.speechrec.OnSpeechRecognitionListener;
import com.suman.trucksharing.speechrec.OnSpeechRecognitionPermissionListener;
import com.suman.trucksharing.speechrec.SpeechRecognition;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Locale;


public class SignUpActivity extends AppCompatActivity implements OnSpeechRecognitionListener, OnSpeechRecognitionPermissionListener {
    UserDBHandler userDBHandler;
    EditText fullname, username, confpass, password, phoneno;
    ImageView pickImage;
    Bitmap bitmap;
    String image;
    TextToSpeech tts;
    SpeechRecognition speechRecognition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
            }
        });
        tts.setLanguage(Locale.US);
        speechRecognition = new SpeechRecognition(this);
        speechRecognition.setSpeechRecognitionPermissionListener( this);
        speechRecognition.setSpeechRecognitionListener( this);

        userDBHandler = new UserDBHandler(this);
        pickImage = findViewById(R.id.pick_image);
        fullname = findViewById(R.id.ed_fullname);
        username = findViewById(R.id.ed_username);
        password = findViewById(R.id.ed_password);
        confpass = findViewById(R.id.conf_pass);
        phoneno = findViewById(R.id.phone_number);
        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(ContextCompat.checkSelfPermission(SignUpActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED)
                {
                    // Permission is not granted
                    ActivityCompat.requestPermissions(SignUpActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                    return;
                }
                Intent intent=new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent,"Browse Image"),666);
            }
        });

        findViewById(R.id.btn_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               signUp();
            }
        });
    }
    void signUp(){
        if (password.getText().toString().equals(confpass.getText().toString()) && !username.getText().toString().isEmpty()) {
            UserModel userModel = new UserModel();
            userModel.setImage(image);
            userModel.setFullname(fullname.getText().toString());
            userModel.setUsername(username.getText().toString());
            userModel.setPassword(password.getText().toString());
            userModel.setPhoneno(phoneno.getText().toString());
            userDBHandler.addUser(userModel);
            Toast.makeText(SignUpActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
            speechRecognition.stopSpeechRecognition();
            finish();
        }else {
            Toast.makeText(SignUpActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null){
            if(requestCode==666 && resultCode==RESULT_OK)
            {
                Uri filepath=data.getData();
                try
                {
                    InputStream inputStream=getContentResolver().openInputStream(filepath);
                    bitmap= BitmapFactory.decodeStream(inputStream);
                    pickImage.setImageBitmap(bitmap);
                    image = encodeBitmapImage(bitmap);
                    Log.e("TAG", image);
                }catch (Exception ex)
                {

                }
            }
        }
    }
    private String encodeBitmapImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] bytesofimage = byteArrayOutputStream.toByteArray();
        return android.util.Base64.encodeToString(bytesofimage, Base64.DEFAULT);
    }

    @Override
    public void OnSpeechRecognitionStarted() {
        Log.e("SIGNUP", "OnSpeechRecognitionStarted: " );
    }

    @Override
    public void OnSpeechRecognitionStopped() {
        Log.e("TAG", "OnSpeechRecognitionStopped: " );
    }

    @Override
    public void OnSpeechRecognitionFinalResult(String finalSentence) {
        Log.e("SIGNUP", "signip--FinalResult: " +finalSentence);
        if (finalSentence.toLowerCase().contains("google")) {
            if (finalSentence.toLowerCase().contains("create")) {
                tts.speak("Ok creating user", TextToSpeech.QUEUE_FLUSH, null);
                signUp();
            } else if (finalSentence.toLowerCase().contains("back")) {
                tts.speak("Ok going back", TextToSpeech.QUEUE_FLUSH, null);
                speechRecognition.stopSpeechRecognition();
                onBackPressed();
            }
        }
        resetRecog();
    }

    @Override
    public void OnSpeechRecognitionCurrentResult(String currentWord) {

    }

    @Override
    public void OnSpeechRecognitionError(int errorCode, String errorMsg) {
resetRecog();
    }

    @Override
    public void onPermissionGranted() {

    }

    @Override
    public void onPermissionDenied() {

    }
    private void resetRecog() {
        speechRecognition.startSpeechRecognition();
    }

    @Override
    protected void onPause() {
        Log.e("SIGNUP", "onPause: " );
        speechRecognition.stopSpeechRecognition();
        super.onPause();
    }

    @Override
    protected void onStart() {
        Log.e("SIGNUP", "onStart: " );
        speechRecognition.startSpeechRecognition();
        super.onStart();
    }
}