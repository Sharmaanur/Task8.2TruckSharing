package com.suman.trucksharing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.suman.trucksharing.DB.ItemModel;
import com.suman.trucksharing.DB.ItemsDBHandler;
import com.suman.trucksharing.adapter.HomeAdapter;
import com.suman.trucksharing.speechrec.OnSpeechRecognitionListener;
import com.suman.trucksharing.speechrec.OnSpeechRecognitionPermissionListener;
import com.suman.trucksharing.speechrec.SpeechRecognition;

import java.util.ArrayList;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity implements OnSpeechRecognitionPermissionListener, OnSpeechRecognitionListener {
    RecyclerView recyclerView;
    HomeAdapter adapter;
    ItemsDBHandler itemsDBHandler;
    String username = "";
    ArrayList<ItemModel> list;
    SpeechRecognition speechRecognition;
    TextToSpeech tts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        recyclerView = findViewById(R.id.listview_item);
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

            }
        });
        tts.setLanguage(Locale.US);
        list = new ArrayList<>();
        speechRecognition = new SpeechRecognition(this);
        speechRecognition.setSpeechRecognitionPermissionListener( this);
        speechRecognition.setSpeechRecognitionListener( this);
        itemsDBHandler = new ItemsDBHandler(this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        list = itemsDBHandler.getAllItems();

        adapter = new HomeAdapter(this, list);
        recyclerView.setAdapter(adapter);
        username = getIntent().getStringExtra("username");
        findViewById(R.id.floatingActionButton1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newOrder();
            }
        });
        adapter.setOnItemClickListener(new HomeAdapter.onItemClickListener() {
            @Override
            public void onitemClick(int position, String name, String date, String time, String weight, String type, String width, String height, String length) {
                Intent intent = new Intent(HomeActivity.this, OrderDetailsActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("name", name);
                intent.putExtra("date", date);
                intent.putExtra("time", time);
                intent.putExtra("weight", weight);
                intent.putExtra("type", type);
                intent.putExtra("width", width);
                intent.putExtra("height", height);
                intent.putExtra("length", length);
                speechRecognition.stopSpeechRecognition();
                startActivity(intent);
            }
        });
    }

    private void newOrder() {
        speechRecognition.stopSpeechRecognition();
        startActivity(new Intent(HomeActivity.this, NewOrderActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_my_order:
                myOrder();
                break;
            case R.id.menu_account:
                myProfile();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void myProfile() {
        speechRecognition.stopSpeechRecognition();
        startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
    }

    private void myOrder() {
        speechRecognition.stopSpeechRecognition();
        startActivity(new Intent(HomeActivity.this, MyOrderActivity.class));
    }

    @Override
    public void onPermissionGranted() {

    }

    @Override
    public void onPermissionDenied() {

    }

    @Override
    public void OnSpeechRecognitionStarted() {
        Log.e("HOME", "OnSpeechRecognitionStarted: ");
    }

    @Override
    public void OnSpeechRecognitionStopped() {
        Log.e("HOME", "OnSpeechRecognitionStopped: " );

    }

    @Override
    public void OnSpeechRecognitionFinalResult(String finalSentence) {
        if (finalSentence.toLowerCase().contains("google")) {
            if (finalSentence.toLowerCase().contains("new order")) {
                tts.speak("opening new order screen", TextToSpeech.QUEUE_FLUSH, null);
                newOrder();
            } else if (finalSentence.toLowerCase().contains("my order")) {
                tts.speak("opening my order ", TextToSpeech.QUEUE_FLUSH, null);
                myOrder();
            } else if (finalSentence.toLowerCase().contains("profile")) {
                tts.speak("opening my profile", TextToSpeech.QUEUE_FLUSH, null);
                myProfile();
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