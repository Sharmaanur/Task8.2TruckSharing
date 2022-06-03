package com.suman.trucksharing;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import com.suman.trucksharing.DB.Data;
import com.suman.trucksharing.DB.ItemModel;
import com.suman.trucksharing.DB.ItemsDBHandler;
import com.suman.trucksharing.speechrec.OnSpeechRecognitionListener;
import com.suman.trucksharing.speechrec.OnSpeechRecognitionPermissionListener;
import com.suman.trucksharing.speechrec.SpeechRecognition;

import java.util.Locale;

public class CreateOrderActivity extends AppCompatActivity implements OnSpeechRecognitionListener, OnSpeechRecognitionPermissionListener {
    Spinner goods, vehicle;
    String name, date, time, pickloc, droploc, fromcord, tocord, goodstype, weight, width, length, height, vehicletype;
    EditText mWeight, mWidth, mLength, mHeight;
    ItemsDBHandler itemsDBHandler;
    SpeechRecognition speechRecognition;
    TextToSpeech tts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order);
        itemsDBHandler = new ItemsDBHandler(this);
        goods = findViewById(R.id.spinner_goods);
        vehicle = findViewById(R.id.spinner_vehicle);
        mWeight = findViewById(R.id.ed_weight);
        mWidth = findViewById(R.id.ed_width);
        mLength = findViewById(R.id.ed_length);
        mHeight = findViewById(R.id.ed_height);

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

            }
        });
        tts.setLanguage(Locale.US);
        speechRecognition = new SpeechRecognition(this);
        speechRecognition.setSpeechRecognitionPermissionListener( this);
        speechRecognition.setSpeechRecognitionListener( this);
        try {
            Intent intent = getIntent();
            if (intent.hasExtra("name") && intent.hasExtra("date")){
                name = intent.getStringExtra("name");
                date = intent.getStringExtra("date");
                time = intent.getStringExtra("time");
                pickloc = intent.getStringExtra("picklocation");
                droploc = intent.getStringExtra("droplocation");
                fromcord = intent.getStringExtra("from");
                tocord = intent.getStringExtra("to");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        goods.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                goodstype = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        vehicle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                vehicletype = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        findViewById(R.id.btn_create_order).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               createOrder();
            }
        });
    }

    private void createOrder() {
        weight = mWeight.getText().toString().trim();
        width = mWidth.getText().toString().trim();
        length = mLength.getText().toString().trim();
        height = mHeight.getText().toString().trim();
        ItemModel itemModel = new ItemModel();
        itemModel.setName(name);
        itemModel.setDate(date);
        itemModel.setTime(time);
        itemModel.setPickuplocation(pickloc);
        itemModel.setDroplocation(droploc);
        itemModel.setFromCoordinates(fromcord);
        itemModel.setToCoordinates(tocord);
        itemModel.setGoodstype(goodstype);
        itemModel.setWeight(weight);
        itemModel.setWidth(width);
        itemModel.setLength(length);
        itemModel.setHeight(height);
        itemModel.setVehicletype(vehicletype);
        itemModel.setUsername(Data.username);
        itemsDBHandler.addItem(itemModel);
        speechRecognition.stopSpeechRecognition();
        startActivity(new Intent(CreateOrderActivity.this, HomeActivity.class));
    }

    @Override
    public void OnSpeechRecognitionStarted() {

    }

    @Override
    public void OnSpeechRecognitionStopped() {

    }

    @Override
    public void OnSpeechRecognitionFinalResult(String finalSentence) {
        if (finalSentence.toLowerCase().contains("create")){
            tts.speak("order created", TextToSpeech.QUEUE_FLUSH, null);
            createOrder();
        }else if (finalSentence.toLowerCase().contains("back")){
            tts.speak("Ok going back", TextToSpeech.QUEUE_FLUSH, null);
            speechRecognition.stopSpeechRecognition();
            onBackPressed();
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
    protected void onPostResume() {
        speechRecognition.startSpeechRecognition();
        Log.e("TAG", "onPostResume: " );
        super.onPostResume();
    }

}