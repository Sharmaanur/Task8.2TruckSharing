package com.suman.trucksharing;

import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.google.android.material.snackbar.Snackbar;
import com.suman.trucksharing.DB.Data;
import com.suman.trucksharing.DB.ItemModel;
import com.suman.trucksharing.DB.UserDBHandler;
import com.suman.trucksharing.DB.UserModel;
import com.suman.trucksharing.speechrec.OnSpeechRecognitionListener;
import com.suman.trucksharing.speechrec.OnSpeechRecognitionPermissionListener;
import com.suman.trucksharing.speechrec.SpeechRecognition;


import java.util.ArrayList;
import java.util.Locale;


public class LoginActivity extends AppCompatActivity implements OnSpeechRecognitionPermissionListener{
    UserDBHandler loginDBHandler;
    EditText username, password;
    SpeechRecognition speechRecognition;
    TextToSpeech tts;
    AudioManager amanager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
             @Override
             public void onInit(int i) {

             }
         });
        tts.setLanguage(Locale.US);

        speechRecognition = new SpeechRecognition(this);
        speechRecognition.setSpeechRecognitionPermissionListener( this);
        speechRecognition.setSpeechRecognitionListener(new OnSpeechRecognitionListener() {
            @Override
            public void OnSpeechRecognitionStarted() {
                Log.e("TAG", "OnSpeechRecognitionStarted: ");
            }

            @Override
            public void OnSpeechRecognitionStopped() {
                Log.e("TAG", "OnSpeechRecognitionStopped: " );

            }

            @Override
            public void OnSpeechRecognitionFinalResult(String finalSentence) {
                Log.e("TAG", "OnSpeechRecognitionFinalResult: " +finalSentence);
                if (finalSentence.toLowerCase().contains("google")){
                    if (finalSentence.toLowerCase().contains("login")){
                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                tts.speak("Ok I'm trying to logging in", TextToSpeech.QUEUE_FLUSH, null);
                            }
                        });
                        login();
                    }else if (finalSentence.toLowerCase().contains("sign")){
                        new Thread( new Runnable() { @Override public void run() {
                            tts.speak("Ok Opening sign up screen", TextToSpeech.QUEUE_FLUSH, null);
                        } } ).start();

                        signup();
                    }else if (finalSentence.toLowerCase().contains("back")){
                        tts.speak("Ok back pressed", TextToSpeech.QUEUE_FLUSH, null);
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
        });

        amanager = (AudioManager)getSystemService(AUDIO_SERVICE);
        amanager.setStreamMute(AudioManager.STREAM_RING, true);

        loginDBHandler = new UserDBHandler(this);
        username = findViewById(R.id.ed_login_username);
        password = findViewById(R.id.ed_login_password);
        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
        findViewById(R.id.btn_signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               signup();
            }
        });

    }

    private void signup() {
        speechRecognition.stopSpeechRecognition();
        startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
    }

    public void login() {
        if (username.getText().toString().isEmpty()){
            username.setError("Enter username");
        }else {
            UserModel userModel = new UserModel();
            userModel.setUsername(username.getText().toString());
            userModel.setPassword(password.getText().toString());
            boolean auth = loginDBHandler.validateUser(userModel);
            if (auth) {
                Data.username = username.getText().toString();
                Data.image = loginDBHandler.getUserInfo(username.getText().toString()).get(0).getImage();
                speechRecognition.stopSpeechRecognition();
                finish();
                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            } else {
                Toast.makeText(LoginActivity.this, "Invalid Username Password", Toast.LENGTH_SHORT).show();
            }
        }
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
        Log.e("TAG", "onPause: " );
        speechRecognition.stopSpeechRecognition();
        super.onPause();
    }

    @Override
    protected void onStart() {
        Log.e("TAG", "onStart: " );
        speechRecognition.startSpeechRecognition();
        super.onStart();
    }
}