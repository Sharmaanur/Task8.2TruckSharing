package com.suman.trucksharing;

import static com.suman.trucksharing.DB.Data.username;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.suman.trucksharing.DB.ItemModel;
import com.suman.trucksharing.DB.ItemsDBHandler;
import com.suman.trucksharing.adapter.HomeAdapter;
import com.suman.trucksharing.speechrec.OnSpeechRecognitionListener;
import com.suman.trucksharing.speechrec.OnSpeechRecognitionPermissionListener;
import com.suman.trucksharing.speechrec.SpeechRecognition;

import java.util.ArrayList;
import java.util.Locale;

public class MyOrderActivity extends AppCompatActivity implements OnSpeechRecognitionPermissionListener, OnSpeechRecognitionListener {
    RecyclerView recyclerView;
    HomeAdapter adapter;
    ItemsDBHandler itemsDBHandler;
    TextToSpeech tts;
    SpeechRecognition speechRecognition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);
        recyclerView = findViewById(R.id.myorder_item);
        itemsDBHandler = new ItemsDBHandler(this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        ArrayList<ItemModel> list = itemsDBHandler.getMyOrder(username);
        adapter = new HomeAdapter(this, list);
        recyclerView.setAdapter(adapter);

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