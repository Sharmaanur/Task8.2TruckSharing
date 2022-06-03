package com.suman.trucksharing;

import static com.suman.trucksharing.DB.Data.username;

import androidx.appcompat.app.AppCompatActivity;
import static com.suman.trucksharing.DB.Data.username;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.suman.trucksharing.DB.Data;
import com.suman.trucksharing.DB.ItemModel;
import com.suman.trucksharing.DB.ItemsDBHandler;
import com.suman.trucksharing.DB.UserDBHandler;
import com.suman.trucksharing.DB.UserModel;
import com.suman.trucksharing.speechrec.OnSpeechRecognitionListener;
import com.suman.trucksharing.speechrec.OnSpeechRecognitionPermissionListener;
import com.suman.trucksharing.speechrec.SpeechRecognition;

import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements OnSpeechRecognitionPermissionListener, OnSpeechRecognitionListener {
    UserDBHandler userDBHandler;
    ItemsDBHandler itemsDBHandler;
    TextView name, edusername, phone, totalOrder;
    CircleImageView avatar;

    TextToSpeech tts;
    SpeechRecognition speechRecognition;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        name = findViewById(R.id.txt_name);
        edusername = findViewById(R.id.txt_username);
        phone = findViewById(R.id.txt_phone);
        avatar = findViewById(R.id.avatar);
        totalOrder = findViewById(R.id.total_order);
        userDBHandler = new UserDBHandler(this);
        itemsDBHandler = new ItemsDBHandler(this);
        ArrayList<ItemModel> list = itemsDBHandler.getMyOrder(username);
        ArrayList<UserModel> userInfo = userDBHandler.getUserInfo(Data.username);

        byte[] decodedString = Base64.decode(userInfo.get(0).getImage(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        avatar.setImageBitmap(decodedByte);

        name.setText(""+userInfo.get(0).getFullname());
        edusername.setText("Username - "+userInfo.get(0).getUsername());
        phone.setText(""+userInfo.get(0).getPhoneno());
        totalOrder.setText("Total ordered "+list.size());
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
            }
        });
        tts.setLanguage(Locale.US);
        speechRecognition = new SpeechRecognition(this);
        speechRecognition.setSpeechRecognitionPermissionListener( this);
        speechRecognition.setSpeechRecognitionListener( this);
    }

    @Override
    public void onPermissionGranted() {

    }

    @Override
    public void onPermissionDenied() {

    }

    @Override
    public void OnSpeechRecognitionStarted() {

    }

    @Override
    public void OnSpeechRecognitionStopped() {

    }

    @Override
    public void OnSpeechRecognitionFinalResult(String finalSentence) {
        Log.e("SIGNUP", "signip--FinalResult: " +finalSentence);
        if (finalSentence.toLowerCase().contains("google")) {
            if (finalSentence.toLowerCase().contains("back")) {
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